package umc.pfc.orientamais.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("InputSanitizer Tests")
class InputSanitizerTest {

  private InputSanitizer sanitizer;

  @BeforeEach
  void setUp() {
    sanitizer = new InputSanitizer();
  }

  @Test
  @DisplayName("Should sanitize HTML allowing safe tags")
  void shouldSanitizeHtmlAllowingSafeTags() {
    String html = "<p>Hello <strong>world</strong>!</p>";
    String result = sanitizer.sanitizeAllowHtml(html);

    assertNotNull(result);
    assertTrue(result.contains("Hello"));
    assertTrue(result.contains("world"));
  }

  @Test
  @DisplayName("Should remove dangerous HTML tags")
  void shouldRemoveDangerousHtmlTags() {
    String dangerous = "<script>alert('xss')</script><p>Safe content</p>";
    String result = sanitizer.sanitizeAllowHtml(dangerous);

    assertFalse(result.contains("script"));
    assertFalse(result.contains("alert"));
    assertTrue(result.contains("Safe content"));
  }

  @Test
  @DisplayName("Should return null when input is null for sanitizeAllowHtml")
  void shouldReturnNullWhenInputIsNull() {
    assertNull(sanitizer.sanitizeAllowHtml(null));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "<script>alert('xss')</script>",
        "<iframe src='evil.com'></iframe>",
        "javascript:alert(1)",
        "<img onerror='alert(1)'>",
        "<svg onload='alert(1)'>"
      })
  @DisplayName("Should detect XSS when HTML not allowed")
  void shouldDetectXssWhenHtmlNotAllowed(String input) {
    assertFalse(sanitizer.isSafeXss(input, false));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Normal text", "Text with numbers 123", "email@example.com"})
  @DisplayName("Should accept safe input when HTML not allowed")
  void shouldAcceptSafeInputWhenHtmlNotAllowed(String input) {
    assertTrue(sanitizer.isSafeXss(input, false));
  }

  @Test
  @DisplayName("Should validate safe HTML when allowHtml is true")
  void shouldValidateSafeHtmlWhenAllowHtmlTrue() {
    String safeHtml = "<p>Hello <strong>world</strong></p>";
    String sanitized = sanitizer.sanitizeAllowHtml(safeHtml);

    assertTrue(sanitizer.isSafeXss(sanitized, true));
  }

  @Test
  @DisplayName("Should reject modified HTML when allowHtml is true")
  void shouldRejectModifiedHtmlWhenAllowHtmlTrue() {
    String html = "<p>Test</p><script>alert('xss')</script>";

    assertFalse(sanitizer.isSafeXss(html, true));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "../../../etc/passwd",
        "..\\..\\windows\\system32",
        "%2e%2e%2f",
        "%2e%2e%5c",
        "file:///../etc/passwd"
      })
  @DisplayName("Should detect path traversal patterns")
  void shouldDetectPathTraversalPatterns(String input) {
    assertFalse(sanitizer.isSafePathTraversal(input));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"/normal/path", "filename.txt", "folder/subfolder/file.pdf", "my-file_123.doc"})
  @DisplayName("Should accept safe paths")
  void shouldAcceptSafePaths(String input) {
    assertTrue(sanitizer.isSafePathTraversal(input));
  }

  @Test
  @DisplayName("Should detect null bytes in Unicode format")
  void shouldDetectNullBytesUnicode() {
    assertFalse(sanitizer.isSafeFromNullBytes("test\u0000malicious"));
  }

  @Test
  @DisplayName("Should accept input without null bytes")
  void shouldAcceptInputWithoutNullBytes() {
    assertTrue(sanitizer.isSafeFromNullBytes("normal text with ñ special chars"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "rm -rf /",
        "cat /etc/passwd",
        "; ls -la",
        "| whoami",
        "&& cat file",
        "|| echo",
        "$(malicious)",
        "`command`"
      })
  @DisplayName("Should detect command injection patterns")
  void shouldDetectCommandInjectionPatterns(String input) {
    assertFalse(sanitizer.isSafeFromCommandInjection(input, false));
  }

  @ParameterizedTest
  @ValueSource(strings = {"normal-filename", "file_name.txt", "My Document.pdf", "123-test"})
  @DisplayName("Should accept safe strings for command injection check")
  void shouldAcceptSafeStringsForCommandInjection(String input) {
    assertTrue(sanitizer.isSafeFromCommandInjection(input, false));
  }

  @Test
  @DisplayName("Should return true for null input in XSS check")
  void shouldReturnTrueForNullInXssCheck() {
    assertTrue(sanitizer.isSafeXss(null, false));
    assertTrue(sanitizer.isSafeXss(null, true));
  }

  @Test
  @DisplayName("Should return true for empty input in XSS check")
  void shouldReturnTrueForEmptyInXssCheck() {
    assertTrue(sanitizer.isSafeXss("", false));
    assertTrue(sanitizer.isSafeXss("", true));
  }

  @Test
  @DisplayName("Should return true for null input in all checks")
  void shouldReturnTrueForNullInAllChecks() {
    assertTrue(sanitizer.isSafePathTraversal(null));
    assertTrue(sanitizer.isSafeFromNullBytes(null));
    assertTrue(sanitizer.isSafeFromCommandInjection(null, false));
    assertTrue(sanitizer.isSafeFromCommandInjection(null, true));
  }

  @Test
  @DisplayName("Should handle edge cases correctly")
  void shouldHandleEdgeCases() {
    assertTrue(sanitizer.isSafePathTraversal(".."));
    assertTrue(sanitizer.isSafePathTraversal("file..txt"));

    assertFalse(sanitizer.isSafePathTraversal("../file"));
    assertFalse(sanitizer.isSafePathTraversal("..\\file"));
  }

  @Test
  @DisplayName("Should allow HTML entities when allowHtml is true")
  void shouldAllowHtmlEntitiesWhenAllowHtmlTrue() {
    // Entidades HTML comuns
    assertTrue(sanitizer.isSafeFromCommandInjection("Hello&nbsp;World", true));
    assertTrue(sanitizer.isSafeFromCommandInjection("&lt;div&gt;", true));
    assertTrue(sanitizer.isSafeFromCommandInjection("&#123;test&#125;", true));
    assertTrue(sanitizer.isSafeFromCommandInjection("&amp; symbol", true));

    // Mas ainda deve bloquear & em contexto de command injection quando allowHtml é false
    assertFalse(sanitizer.isSafeFromCommandInjection("test && command", false));
    assertFalse(sanitizer.isSafeFromCommandInjection("test || command", false));
  }

  @Test
  @DisplayName("Should detect command injection even with allowHtml true")
  void shouldDetectCommandInjectionWithAllowHtmlTrue() {
    // Command injection patterns devem ser bloqueados mesmo com allowHtml = true
    assertFalse(sanitizer.isSafeFromCommandInjection("test | whoami", true));
    assertFalse(sanitizer.isSafeFromCommandInjection("test `command`", true));
    assertFalse(sanitizer.isSafeFromCommandInjection("$(malicious)", true));
  }
}
