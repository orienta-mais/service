package umc.pfc.orientamais.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputSanitizerTest {

  @Test
  void testSanitize_nullInput_returnsNull() {
    assertNull(InputSanitizer.sanitize(null));
  }

  @Test
  void testSanitize_emptyInput_returnsEmpty() {
    assertEquals("", InputSanitizer.sanitize(""));
  }

  @Test
  void testSanitize_normalInput_trimmed() {
    assertEquals("test", InputSanitizer.sanitize("  test  "));
  }

  @Test
  void testSanitize_multipleSpaces_normalized() {
    assertEquals("test string", InputSanitizer.sanitize("test    string"));
  }

  @Test
  void testSanitize_nullBytes_removed() {
    String input = "test\u0000malicious";
    String result = InputSanitizer.sanitize(input);
    assertFalse(result.contains("\u0000"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "'; DROP TABLE users--",
        "1' OR '1'='1",
        "admin'--",
        "' UNION SELECT * FROM passwords--",
        "'; EXEC sp_MSForEachTable 'DROP TABLE ?'--",
        "1; DELETE FROM users WHERE '1'='1",
        "test'; INSERT INTO admin VALUES('hacker')--"
      })
  void testIsSafeFromSqlInjection_maliciousInput_returnsFalse(String input) {
    assertFalse(
        InputSanitizer.isSafeFromSqlInjection(input), "Should detect SQL injection: " + input);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "João Silva",
        "user@example.com",
        "Normal text without special chars",
        "Test-123",
        "Some description with punctuation!"
      })
  void testIsSafeFromSqlInjection_safeInput_returnsTrue(String input) {
    assertTrue(InputSanitizer.isSafeFromSqlInjection(input), "Should accept safe input: " + input);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "<script>alert('XSS')</script>",
        "<img src=x onerror=alert('XSS')>",
        "<iframe src='http://malicious.com'></iframe>",
        "javascript:alert('XSS')",
        "<body onload=alert('XSS')>",
        "<div onclick='malicious()'>",
        "eval(document.cookie)",
        "expression(alert('XSS'))"
      })
  void testIsSafeFromXss_maliciousInput_returnsFalse(String input) {
    assertFalse(InputSanitizer.isSafeFromXss(input), "Should detect XSS: " + input);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Normal text",
        "user@example.com",
        "Description without tags",
        "Text with (parentheses) and [brackets]"
      })
  void testIsSafeFromXss_safeInput_returnsTrue(String input) {
    assertTrue(InputSanitizer.isSafeFromXss(input), "Should accept safe input: " + input);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "*(objectClass=*)",
        "admin)(|(password=*))",
        "user*",
        "test\\user",
        "(cn=*)",
        "user&admin",
        "test|malicious"
      })
  void testIsSafeFromLdapInjection_maliciousInput_returnsFalse(String input) {
    assertFalse(
        InputSanitizer.isSafeFromLdapInjection(input), "Should detect LDAP injection: " + input);
  }

  @ParameterizedTest
  @ValueSource(strings = {"normaluser", "user.name", "test-user", "user_123"})
  void testIsSafeFromLdapInjection_safeInput_returnsTrue(String input) {
    assertTrue(InputSanitizer.isSafeFromLdapInjection(input), "Should accept safe input: " + input);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "../../../etc/passwd",
        "..\\..\\windows\\system32",
        "%2e%2e/etc/passwd",
        "%252e%252e%252f",
        "test/../../../secret"
      })
  void testIsSafeFromPathTraversal_maliciousInput_returnsFalse(String input) {
    assertFalse(
        InputSanitizer.isSafeFromPathTraversal(input), "Should detect path traversal: " + input);
  }

  @ParameterizedTest
  @ValueSource(strings = {"normalfile.txt", "user/documents/file.pdf", "images/photo.jpg"})
  void testIsSafeFromPathTraversal_safeInput_returnsTrue(String input) {
    assertTrue(InputSanitizer.isSafeFromPathTraversal(input), "Should accept safe input: " + input);
  }

  @Test
  void testIsSafeFromNullBytes_withNullByte_returnsFalse() {
    assertFalse(InputSanitizer.isSafeFromNullBytes("test\u0000malicious"));
  }

  @Test
  void testIsSafeFromNullBytes_withEncodedNullByte_returnsFalse() {
    assertFalse(InputSanitizer.isSafeFromNullBytes("test%00malicious"));
  }

  @Test
  void testIsSafeFromNullBytes_safeInput_returnsTrue() {
    assertTrue(InputSanitizer.isSafeFromNullBytes("normal text"));
  }

  @Test
  void testIsComprehensiveSafe_allAttacks_returnsFalse() {
    String[] attacks = {
      "'; DROP TABLE--",
      "<script>alert('XSS')</script>",
      "../../../etc/passwd",
      "test\u0000malicious"
    };

    for (String attack : attacks) {
      assertFalse(InputSanitizer.isComprehensiveSafe(attack), "Should detect attack: " + attack);
    }
  }

  @Test
  void testIsComprehensiveSafe_safeInput_returnsTrue() {
    String[] safeInputs = {"João Silva", "user@example.com", "Normal description text", "Test-123"};

    for (String safe : safeInputs) {
      assertTrue(InputSanitizer.isComprehensiveSafe(safe), "Should accept safe input: " + safe);
    }
  }

  @Test
  void testStripHtmlTags_removesTags() {
    String input = "<p>Hello</p><script>alert('XSS')</script>";
    String result = InputSanitizer.stripHtmlTags(input);
    assertEquals("Helloalert('XSS')", result);
    assertFalse(result.contains("<"));
    assertFalse(result.contains(">"));
  }

  @Test
  void testStripHtmlTags_nullInput_returnsNull() {
    assertNull(InputSanitizer.stripHtmlTags(null));
  }

  @Test
  void testEscapeHtml_escapesSpecialChars() {
    String input = "<script>alert(\"XSS\")</script>";
    String result = InputSanitizer.escapeHtml(input);

    assertFalse(result.contains("<"));
    assertFalse(result.contains(">"));
    assertTrue(result.contains("&lt;"));
    assertTrue(result.contains("&gt;"));
    assertTrue(result.contains("&quot;"));
  }

  @Test
  void testEscapeHtml_allSpecialChars() {
    String input = "&<>\"'/";
    String result = InputSanitizer.escapeHtml(input);

    assertEquals("&amp;&lt;&gt;&quot;&#x27;&#x2F;", result);
  }

  @Test
  void testEscapeHtml_nullInput_returnsNull() {
    assertNull(InputSanitizer.escapeHtml(null));
  }

  @Test
  void testSanitizeAndValidate_safeInput_returnsSanitized() {
    String input = "  Normal text  ";
    String result = InputSanitizer.sanitizeAndValidate(input);
    assertEquals("Normal text", result);
  }

  @Test
  void testSanitizeAndValidate_maliciousInput_throwsException() {
    String[] attacks = {"'; DROP TABLE--", "<script>alert('XSS')</script>", "../../../etc/passwd"};

    for (String attack : attacks) {
      assertThrows(
          IllegalArgumentException.class,
          () -> InputSanitizer.sanitizeAndValidate(attack),
          "Should throw exception for: " + attack);
    }
  }

  @Test
  void testSanitizeAndValidate_nullInput_returnsNull() {
    assertNull(InputSanitizer.sanitizeAndValidate(null));
  }

  @Test
  void testEmptyString_allChecks_returnsTrue() {
    assertTrue(InputSanitizer.isSafeFromSqlInjection(""));
    assertTrue(InputSanitizer.isSafeFromXss(""));
    assertTrue(InputSanitizer.isSafeFromLdapInjection(""));
    assertTrue(InputSanitizer.isSafeFromPathTraversal(""));
    assertTrue(InputSanitizer.isSafeFromNullBytes(""));
    assertTrue(InputSanitizer.isComprehensiveSafe(""));
  }

  @Test
  void testNullString_allChecks_returnsTrue() {
    assertTrue(InputSanitizer.isSafeFromSqlInjection(null));
    assertTrue(InputSanitizer.isSafeFromXss(null));
    assertTrue(InputSanitizer.isSafeFromLdapInjection(null));
    assertTrue(InputSanitizer.isSafeFromPathTraversal(null));
    assertTrue(InputSanitizer.isSafeFromNullBytes(null));
    assertTrue(InputSanitizer.isComprehensiveSafe(null));
  }

  @Test
  void testRealWorldInputs_portugueseNames_safe() {
    String[] names = {
      "João Silva", "Maria José de Oliveira", "José da Silva-Santos", "Ana D'Ávila"
    };

    for (String name : names) {
      assertTrue(
          InputSanitizer.isComprehensiveSafe(name), "Should accept Portuguese name: " + name);
    }
  }

  @Test
  void testRealWorldInputs_emails_safe() {
    String[] emails = {
      "user@example.com", "test.user+tag@domain.co.uk", "user_123@test-domain.org"
    };

    for (String email : emails) {
      assertTrue(InputSanitizer.isComprehensiveSafe(email), "Should accept valid email: " + email);
    }
  }

  @Test
  void testRealWorldInputs_descriptions_safe() {
    String description =
        "Sou um mentor com mais de 10 anos de experiência em tecnologia. "
            + "Trabalho com Java, Spring Boot e arquitetura de microsserviços. "
            + "Adoro ensinar e compartilhar conhecimento!";

    assertTrue(InputSanitizer.isComprehensiveSafe(description));
  }

  @Test
  void testRealWorldInputs_socialMedia_safe() {
    String[] socialMedias = {
      "@username",
      "linkedin.com/in/user",
      "github.com/developer",
      "twitter.com/user, instagram.com/user"
    };

    for (String social : socialMedias) {
      assertTrue(
          InputSanitizer.isComprehensiveSafe(social), "Should accept social media: " + social);
    }
  }
}
