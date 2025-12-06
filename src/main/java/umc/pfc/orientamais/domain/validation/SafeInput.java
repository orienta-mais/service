package umc.pfc.orientamais.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure input is safe from injection attacks. Validates against
 * SQL injection, XSS, LDAP injection, path traversal, and null bytes.
 */
@Documented
@Constraint(validatedBy = SafeInputValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeInput {

  String message() default "Input contém caracteres ou padrões suspeitos";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /** Specify which types of injection to check for. By default, all checks are enabled. */
  InjectionType[] checkFor() default {
    InjectionType.SQL_INJECTION,
    InjectionType.XSS,
    InjectionType.PATH_TRAVERSAL,
    InjectionType.NULL_BYTES
  };

  enum InjectionType {
    SQL_INJECTION,
    XSS,
    LDAP_INJECTION,
    PATH_TRAVERSAL,
    NULL_BYTES
  }
}
