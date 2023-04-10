package academy.devdojo.springboot2.client;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {

    public static void main(String[] args) {
        //#################
        // GET
        //##############################################################################################################
        ResponseEntity<Anime> entityIdOnUrl = new RestTemplate().getForEntity("http://localhost:8080/animes/2", Anime.class);
        log.info("entityIdOnUrl: {}", entityIdOnUrl);

        ResponseEntity<Anime> entityIdAsParameter = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 4);
        log.info("entityIdAsParameter: {}" ,entityIdAsParameter);

        Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/2", Anime.class);
        log.info("object: {}", object);

        Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
        log.info("animes: {}", Arrays.toString(animes));

        ResponseEntity<List<Anime>> animeList = new RestTemplate().exchange(
                "http://localhost:8080/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        log.info("animeList: {}", animeList.getBody());
        //##############################################################################################################

        //#################
        // POST
        //##############################################################################################################
        Anime kingdom = Anime.builder().name("kingdom").build();
        Anime kingdomSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdom, Anime.class);
        log.info("kingdomSaved: {}", kingdomSaved);

        Anime samuraiChamploo = Anime.builder().name("Samurai Champloo").build();
        ResponseEntity<Anime> samuraiChamplooSaved = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.POST,
                new HttpEntity<>(samuraiChamploo),
                new ParameterizedTypeReference<>() {
                });
        log.info("samuraiChamplooSaved: {}", samuraiChamplooSaved.getBody());
        //##############################################################################################################

        //#################
        // PUT
        //##############################################################################################################
        Anime animeToBeUpdated = samuraiChamplooSaved.getBody();
        animeToBeUpdated.setName("samurai champloo 2");

        ResponseEntity<Void> samuraiChamplooUpdated = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.PUT,
                new HttpEntity<>(animeToBeUpdated),
                Void.class);
        log.info(samuraiChamplooUpdated);
        //##############################################################################################################

        //#################
        // DELETE
        //##############################################################################################################
        ResponseEntity<Void> samuraiChamplooDeleted = new RestTemplate().exchange("http://localhost:8080/animes/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                animeToBeUpdated.getId());
        log.info(samuraiChamplooDeleted);
        //##############################################################################################################
    }
}
