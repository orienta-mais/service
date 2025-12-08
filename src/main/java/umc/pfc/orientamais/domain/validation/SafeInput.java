package umc.pfc.orientamais.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SafeInputValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeInput {

  String message() default "Input contém caracteres ou padrões suspeitos";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  InjectionType[] checkFor() default {
    InjectionType.SQL_INJECTION,
    InjectionType.XSS,
    InjectionType.PATH_TRAVERSAL,
    InjectionType.NULL_BYTES
  };

  boolean allowHtml() default false;

  enum InjectionType {
    SQL_INJECTION,
    XSS,
    LDAP_INJECTION,
    PATH_TRAVERSAL,
    COMMAND_INJECTION,
    NULL_BYTES
  }
}
