package umc.pfc.orientamais.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.domain.utils.InputSanitizer;

@Component
@RequiredArgsConstructor
public class SafeInputValidator implements ConstraintValidator<SafeInput, String> {

  private final InputSanitizer sanitizer;

  private Set<SafeInput.InjectionType> checksToPerform;
  private boolean allowHtml;

  @Override
  public void initialize(SafeInput constraintAnnotation) {
    this.checksToPerform =
        Arrays.stream(constraintAnnotation.checkFor()).collect(Collectors.toSet());
    this.allowHtml = constraintAnnotation.allowHtml();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true;
    }

    if (checksToPerform.contains(SafeInput.InjectionType.SQL_INJECTION)) {
      String lower = value.toLowerCase();

      if (allowHtml) {
        if (containsSqlInjectionPatterns(lower)) {
          context.disableDefaultConstraintViolation();
          context
              .buildConstraintViolationWithTemplate(
                  "Input contém padrões possivelmente maliciosos (SQL)")
              .addConstraintViolation();
          return false;
        }
      } else {
        if (lower.contains(";")
            || lower.contains("--")
            || lower.contains("/*")
            || lower.contains("*/")
            || containsSqlInjectionPatterns(lower)) {
          context.disableDefaultConstraintViolation();
          context
              .buildConstraintViolationWithTemplate(
                  "Input contém padrões possivelmente maliciosos (SQL)")
              .addConstraintViolation();
          return false;
        }
      }
    }

    if (checksToPerform.contains(SafeInput.InjectionType.XSS)) {
      boolean safe = sanitizer.isSafeXss(value, allowHtml);
      if (!safe) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Input contém padrões de XSS")
            .addConstraintViolation();
        return false;
      }
    }

    if (checksToPerform.contains(SafeInput.InjectionType.PATH_TRAVERSAL)) {
      if (!sanitizer.isSafePathTraversal(value)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Input contém padrões de path traversal")
            .addConstraintViolation();
        return false;
      }
    }

    if (checksToPerform.contains(SafeInput.InjectionType.NULL_BYTES)) {
      if (!sanitizer.isSafeFromNullBytes(value)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Input contém null bytes")
            .addConstraintViolation();
        return false;
      }
    }

    if (checksToPerform.contains(SafeInput.InjectionType.COMMAND_INJECTION)) {
      if (!sanitizer.isSafeFromCommandInjection(value, allowHtml)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                "Input contém padrões possivelmente de command injection")
            .addConstraintViolation();
        return false;
      }
    }

    return true;
  }

  private boolean containsSqlInjectionPatterns(String lowerValue) {
    String[] sqlKeywords = {
      "union select",
      "union all select",
      "' or '1'='1",
      "\" or \"1\"=\"1",
      "' or 1=1",
      "\" or 1=1",
      "; drop ",
      "; delete ",
      "; update ",
      "; insert ",
      "; exec",
      "; execute",
      "exec(",
      "execute(",
      "xp_cmdshell",
      "sp_executesql",
      "information_schema",
      "sys.tables",
      "sys.columns"
    };

    for (String pattern : sqlKeywords) {
      if (lowerValue.contains(pattern)) {
        return true;
      }
    }

    if (lowerValue.matches(".*;\\s*(select|insert|update|delete|drop|create|alter|exec).*")) {
      return true;
    }

    return lowerValue.matches(".*--[^>].*") || lowerValue.matches(".*/\\*.*(?!-->).*");
  }
}
