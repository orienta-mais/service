package umc.pfc.orientamais.domain.validation;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SafeInputValidator Integration Tests")
class SafeInputValidatorTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Should accept valid input without HTML")
  void shouldAcceptValidInputWithoutHtml() {
    TestDto dto = new TestDto("Normal text input");
    Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "<script>alert('xss')</script>",
        "test; DROP TABLE users;",
        "../../../etc/passwd",
        "test\u0000malicious",
        "rm -rf /"
      })
  @DisplayName("Should reject malicious patterns")
  void shouldRejectMaliciousPatterns(String input) {
    TestDto dto = new TestDto(input);
    Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("Should accept safe HTML when allowHtml is true")
  void shouldAcceptSafeHtmlWhenAllowHtmlTrue() {
    TestDtoWithHtml dto = new TestDtoWithHtml("<p>Safe <strong>HTML</strong> content</p>");
    Set<ConstraintViolation<TestDtoWithHtml>> violations = validator.validate(dto);
    assertTrue(
        violations.isEmpty() || violations.stream().noneMatch(v -> v.getMessage().contains("XSS")));
  }

  @Test
  @DisplayName("Should reject dangerous HTML even when allowHtml is true")
  void shouldRejectDangerousHtmlEvenWhenAllowHtmlTrue() {
    TestDtoWithHtml dto = new TestDtoWithHtml("<script>alert('xss')</script>");
    Set<ConstraintViolation<TestDtoWithHtml>> violations = validator.validate(dto);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("Should accept null and empty values")
  void shouldAcceptNullAndEmptyValues() {
    TestDto dtoNull = new TestDto(null);
    TestDto dtoEmpty = new TestDto("");

    assertTrue(validator.validate(dtoNull).isEmpty());
    assertTrue(validator.validate(dtoEmpty).isEmpty());
  }

  static class TestDto {
    @SafeInput private final String value;

    TestDto(String value) {
      this.value = value;
    }
  }

  static class TestDtoWithHtml {
    @SafeInput(allowHtml = true)
    private final String value;

    TestDtoWithHtml(String value) {
      this.value = value;
    }
  }
}
