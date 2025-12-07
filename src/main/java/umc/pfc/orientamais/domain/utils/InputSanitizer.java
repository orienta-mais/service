package umc.pfc.orientamais.domain.utils;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing user inputs to prevent injection attacks. Provides comprehensive
 * validation and sanitization methods.
 */
public class InputSanitizer {

  private static final Pattern SQL_INJECTION_PATTERN =
      Pattern.compile(
          "('.*(--|#|/\\*|\\*/|xp_|sp_|exec|execute|select|insert|update|delete|drop|create|alter|truncate|union|declare|cast|convert).*')|"
              + "(\\b(select|insert|update|delete|drop|create|alter|truncate|union|declare|cast|convert|exec|execute|xp_|sp_)\\b)",
          Pattern.CASE_INSENSITIVE);

  private static final Pattern XSS_PATTERN =
      Pattern.compile(
          "(<script[^>]*>.*?</script>)|"
              + "(<iframe[^>]*>.*?</iframe>)|"
              + "(javascript:)|"
              + "(on\\w+\\s*=)|"
              + "(<\\w+[^>]*\\son\\w+\\s*=)|"
              + "(eval\\s*\\()|"
              + "(expression\\s*\\()",
          Pattern.CASE_INSENSITIVE);


  private static final Pattern PATH_TRAVERSAL_PATTERN =
      Pattern.compile("(\\.\\./)|(\\.\\\\)|(%2e%2e)|(%252e%252e)");

  private static final Pattern NULL_BYTE_PATTERN = Pattern.compile("\\x00|%00");

  private static final Pattern COMMAND_INJECTION_PATTERN =
      Pattern.compile(
          "(;\\s*(rm|cat|ls|pwd|whoami|id|uname|wget|curl|bash|sh|cmd|powershell|eval|exec)\\s)|"
              + "(\\|\\s*(rm|cat|ls|pwd|whoami|id|uname|wget|curl|bash|sh|cmd|powershell|eval|exec)\\s)|"
              + "(&&\\s*(rm|cat|ls|pwd|whoami|id|uname|wget|curl|bash|sh|cmd|powershell|eval|exec)\\s)|"
              + "(\\$\\(.*\\))|"
              + "(`.*`)|"
              + "(\\|\\||&&)",
          Pattern.CASE_INSENSITIVE);

  private InputSanitizer() {
    // Utility class
  }

  /**
   * Sanitizes a string by removing potentially dangerous characters. Preserves safe characters for
   * text content.
   *
   * @param input the input string to sanitize
   * @return sanitized string or null if input is null
   */
  public static String sanitize(String input) {
    if (input == null) {
      return null;
    }

    String sanitized = input.replaceAll("\\x00", "");

    sanitized = sanitized.trim();

    sanitized = sanitized.replaceAll("\\s+", " ");

    return sanitized;
  }

  /**
   * Validates that the input doesn't contain SQL injection patterns.
   *
   * @param input the input to validate
   * @return true if no SQL injection patterns are detected
   */
  public static boolean isSafeFromSqlInjection(String input) {
    if (input == null || input.isEmpty()) {
      return true;
    }
    return !SQL_INJECTION_PATTERN.matcher(input).find();
  }

  /**
   * Validates that the input doesn't contain XSS patterns.
   *
   * @param input the input to validate
   * @return true if no XSS patterns are detected
   */
  public static boolean isSafeFromXss(String input) {
    if (input == null || input.isEmpty()) {
      return true;
    }
    return !XSS_PATTERN.matcher(input).find();
  }


  /**
   * Validates that the input doesn't contain path traversal patterns.
   *
   * @param input the input to validate
   * @return true if no path traversal patterns are detected
   */
  public static boolean isSafeFromPathTraversal(String input) {
    if (input == null || input.isEmpty()) {
      return true;
    }
    return !PATH_TRAVERSAL_PATTERN.matcher(input).find();
  }

  /**
   * Validates that the input doesn't contain null bytes.
   *
   * @param input the input to validate
   * @return true if no null bytes are detected
   */
  public static boolean isSafeFromNullBytes(String input) {
    if (input == null || input.isEmpty()) {
      return true;
    }
    return !NULL_BYTE_PATTERN.matcher(input).find();
  }

  /**
   * Validates that the input doesn't contain command injection patterns.
   *
   * @param input the input to validate
   * @return true if no command injection patterns are detected
   */
  public static boolean isSafeFromCommandInjection(String input) {
    if (input == null || input.isEmpty()) {
      return true;
    }
    return !COMMAND_INJECTION_PATTERN.matcher(input).find();
  }

  /**
   * Performs comprehensive validation against all known injection patterns.
   *
   * @param input the input to validate
   * @return true if the input is safe from all injection attacks
   */
  public static boolean isComprehensiveSafe(String input) {
    return isSafeFromSqlInjection(input)
        && isSafeFromXss(input)
        && isSafeFromPathTraversal(input)
        && isSafeFromNullBytes(input)
        && isSafeFromCommandInjection(input);
  }

  /**
   * Sanitizes and validates text input for safe storage. Applies both sanitization and validation.
   *
   * @param input the input to sanitize and validate
   * @return sanitized input
   * @throws IllegalArgumentException if input contains dangerous patterns
   */
  public static String sanitizeAndValidate(String input) {
    if (input == null) {
      return null;
    }

    String sanitized = sanitize(input);

    if (!isComprehensiveSafe(sanitized)) {
      throw new IllegalArgumentException("Input contém caracteres ou padrões suspeitos de injeção");
    }

    return sanitized;
  }

  /**
   * Removes HTML/XML tags from input.
   *
   * @param input the input string
   * @return string without HTML/XML tags
   */
  public static String stripHtmlTags(String input) {
    if (input == null) {
      return null;
    }
    return input.replaceAll("<[^>]*>", "");
  }

  /**
   * Escapes HTML special characters to prevent XSS.
   *
   * @param input the input string
   * @return HTML-escaped string
   */
  public static String escapeHtml(String input) {
    if (input == null) {
      return null;
    }

    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
        .replace("/", "&#x2F;");
  }
}
