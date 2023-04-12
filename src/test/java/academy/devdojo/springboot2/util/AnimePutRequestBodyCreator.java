package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {

    public static AnimePutRequestBody createAnimePutRequestBody() {
        Anime anime = AnimeCreator.createValidUpdatedAnime();
        return AnimePutRequestBody.builder()
                .id(anime.getId())
                .name(anime.getName())
                .build();
    }
}
