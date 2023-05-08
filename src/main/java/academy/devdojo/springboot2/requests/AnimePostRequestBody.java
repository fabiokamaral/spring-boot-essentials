package academy.devdojo.springboot2.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePostRequestBody {

    @NotEmpty(message = "The anime name cannot be empty")
    @Schema(description = "This is de Anime's name",
            example = "Tensei Shittara Slime Datta Ken",
            requiredMode = REQUIRED)
    private String name;

}
