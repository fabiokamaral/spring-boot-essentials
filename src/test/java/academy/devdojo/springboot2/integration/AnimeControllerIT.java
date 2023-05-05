package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.DevDojoUserCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

//Necessario para testes de integração onde o objetivo é testar o fluxo completo
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {

    @Autowired
    @Qualifier("testRestTemplateUser")
    private TestRestTemplate testRestTemplateUser;

    @Autowired
    @Qualifier("testRestTemplateAdmin")
    private TestRestTemplate testRestTemplateAdmin;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private DevDojoUserRepository devDojoUserRepository;

    private static final DevDojoUser USER = DevDojoUserCreator.createDevDojoUserWithRoleUser();
    private static final DevDojoUser ADMIN = DevDojoUserCreator.createDevDojoUserWithRoleAdmin();

    @BeforeEach
    void setUp() {
        animeRepository.deleteAll();
        devDojoUserRepository.deleteAll();
    }

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean("testRestTemplateUser")
        public TestRestTemplate testRestTemplateUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("test", "test");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean("testRestTemplateAdmin")
        public TestRestTemplate testRestTemplateAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("admin", "test");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimeInsidePageObject_WhenSuccessful() {
        devDojoUserRepository.save(USER);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = savedAnime.getName();

        //Por padrão o testRestTemplate consegue encontrar o localhost e a porta em que os server está rodando
        PageableResponse<Anime> animePage = testRestTemplateUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAll returns list of anime when successful")
    void listAll_ReturnsListOfAnime_WhenSuccessful() {
        devDojoUserRepository.save(USER);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = savedAnime.getName();

        //Por padrão o testRestTemplate consegue encontrar o localhost e a porta em que os server está rodando
        List<Anime> animeList = testRestTemplateUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animeList)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        devDojoUserRepository.save(USER);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        Long expectedId = savedAnime.getId();

        Anime anime = testRestTemplateUser.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns anime when successful")
    void findByName_ReturnsAnime_WhenSuccessful() {
        devDojoUserRepository.save(USER);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = savedAnime.getName();
        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animeList = testRestTemplateUser.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animeList)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns a empty list of anime when anime is not found")
    void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
        devDojoUserRepository.save(USER);
        List<Anime> animeList = testRestTemplateUser.exchange("/animes/find?name=dbz", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animeList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        devDojoUserRepository.save(ADMIN);
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateAdmin.postForEntity("/animes/admin",
                animePostRequestBody, Anime.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("save returns 403 when user is not admin")
    void save_Returns403_WhenUserIsNotAdmin() {
        devDojoUserRepository.save(USER);
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateUser.postForEntity("/animes/admin",
                animePostRequestBody, Anime.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {
        devDojoUserRepository.save(USER);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        ResponseEntity<Void> responseEntity = testRestTemplateUser.exchange("/animes", HttpMethod.PUT,
                new HttpEntity<>(savedAnime), Void.class);

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful() {
        devDojoUserRepository.save(ADMIN);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        ResponseEntity<Void> responseEntity = testRestTemplateAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE,
                null, Void.class, savedAnime.getId());

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {
        devDojoUserRepository.save(USER);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        ResponseEntity<Void> responseEntity = testRestTemplateUser.exchange("/animes/admin/{id}", HttpMethod.DELETE,
                null, Void.class, savedAnime.getId());

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
