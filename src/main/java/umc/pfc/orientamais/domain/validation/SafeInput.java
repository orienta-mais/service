package umc.pfc.orientamais.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure input is safe from injection attacks. Validates against
 * SQL injection, XSS, path traversal, command injection, and null bytes.
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
    InjectionType.NULL_BYTES,
    InjectionType.COMMAND_INJECTION
  };

  /**
   * Allow HTML tags in the input. When true, XSS check will be more lenient and allow safe HTML.
   * Default is false.
   */
  boolean allowHtml() default false;

  enum InjectionType {
    SQL_INJECTION,
    XSS,
    PATH_TRAVERSAL,
    NULL_BYTES,
    COMMAND_INJECTION
  }
}
