package academy.devdojo.springboot2.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Classe que padroniza a resposta para erros de BadRequest lançadas pelos controllers
 */

@Getter
@SuperBuilder
public class BadRequestExceptionDetails extends ExceptionDetails{

}
