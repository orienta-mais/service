package umc.pfc.orientamais.application.service.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;

@ExtendWith(MockitoExtension.class)
class EmailTemplateBuilderTest {

  @Mock private ResourceLoader resourceLoader;

  @Mock private Resource resource;

  @InjectMocks private EmailTemplateBuilder emailTemplateBuilder;

  @BeforeEach
  void setUp() {
    // No-op: dependencies mocked via annotations
  }

  @Test
  void buildMentorRegisterEmailShouldReplacePlaceholder() throws IOException {
    when(resourceLoader.getResource(eq("classpath:templates/mentor_register.html")))
        .thenReturn(resource);
    when(resource.getInputStream()).thenReturn(stream("<html>${link}</html>"));

    String result = emailTemplateBuilder.buildMentorRegisterEmail("http://link");

    assertEquals("<html>http://link</html>", result);
  }

  @Test
  void buildMentorRegisterEmailShouldThrowWhenResourceFails() throws IOException {
    when(resourceLoader.getResource(eq("classpath:templates/mentor_register.html")))
        .thenReturn(resource);
    when(resource.getInputStream()).thenThrow(new IOException("fail"));

    InternalErrorException exception =
        assertThrows(
            InternalErrorException.class,
            () -> emailTemplateBuilder.buildMentorRegisterEmail("link"));
    assertEquals("Erro ao carregar template de e-mail", exception.getMessage());
  }

  @Test
  void buildPasswordResetEmailShouldReplacePlaceholder() throws IOException {
    when(resourceLoader.getResource(eq("classpath:templates/password_reset.html")))
        .thenReturn(resource);
    when(resource.getInputStream()).thenReturn(stream("<body>${link}</body>"));

    String result = emailTemplateBuilder.buildPasswordResetEmail("reset-link");

    assertEquals("<body>reset-link</body>", result);
  }

  @Test
  void buildPasswordResetEmailShouldThrowWhenResourceFails() throws IOException {
    when(resourceLoader.getResource(eq("classpath:templates/password_reset.html")))
        .thenReturn(resource);
    when(resource.getInputStream()).thenThrow(new IOException("fail"));

    assertThrows(
        InternalErrorException.class, () -> emailTemplateBuilder.buildPasswordResetEmail("reset"));
  }

  @Test
  void buildPasswordChangedEmailShouldReturnTemplateContent() throws IOException {
    when(resourceLoader.getResource(eq("classpath:templates/password_changed.html")))
        .thenReturn(resource);
    when(resource.getInputStream()).thenReturn(stream("<p>Password changed</p>"));

    String result = emailTemplateBuilder.buildPasswordChangedEmail();

    assertEquals("<p>Password changed</p>", result);
  }

  @Test
  void buildPasswordChangedEmailShouldThrowWhenResourceFails() throws IOException {
    when(resourceLoader.getResource(eq("classpath:templates/password_changed.html")))
        .thenReturn(resource);
    when(resource.getInputStream()).thenThrow(new IOException("fail"));

    assertThrows(InternalErrorException.class, emailTemplateBuilder::buildPasswordChangedEmail);
  }

  private ByteArrayInputStream stream(String value) {
    return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
  }
}
