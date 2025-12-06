package umc.pfc.orientamais.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import umc.pfc.orientamais.domain.utils.InputSanitizer;

/**
 * Validator implementation for the @SafeInput annotation. Checks input strings against various
 * injection attack patterns.
 */
public class SafeInputValidator implements ConstraintValidator<SafeInput, String> {

  private Set<SafeInput.InjectionType> checksToPerform;

  @Override
  public void initialize(SafeInput constraintAnnotation) {
    this.checksToPerform =
        Arrays.stream(constraintAnnotation.checkFor()).collect(Collectors.toSet());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // Null or empty values are considered valid (use @NotNull/@NotBlank for null checks)
    if (value == null || value.isEmpty()) {
      return true;
    }

    boolean isValid = true;

    if (checksToPerform.contains(SafeInput.InjectionType.SQL_INJECTION)) {
      if (!InputSanitizer.isSafeFromSqlInjection(value)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Input contém padrões de SQL injection")
            .addConstraintViolation();
        isValid = false;
      }
    }

    if (checksToPerform.contains(SafeInput.InjectionType.XSS)) {
      if (!InputSanitizer.isSafeFromXss(value)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Input contém padrões de XSS")
            .addConstraintViolation();
        isValid = false;
      }
    }

    if (checksToPerform.contains(SafeInput.InjectionType.LDAP_INJECTION)) {
      if (!InputSanitizer.isSafeFromLdapInjection(value)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Input contém padrões de LDAP injection")
            .addConstraintViolation();
        isValid = false;
      }
    }

    if (checksToPerform.contains(SafeInput.InjectionType.PATH_TRAVERSAL)) {
      if (!InputSanitizer.isSafeFromPathTraversal(value)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Input contém padrões de path traversal")
            .addConstraintViolation();
        isValid = false;
      }
    }

    if (checksToPerform.contains(SafeInput.InjectionType.NULL_BYTES)) {
      if (!InputSanitizer.isSafeFromNullBytes(value)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Input contém null bytes")
            .addConstraintViolation();
        isValid = false;
      }
    }

    return isValid;
  }
}
