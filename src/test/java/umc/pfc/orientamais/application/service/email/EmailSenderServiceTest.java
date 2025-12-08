package umc.pfc.orientamais.application.service.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

  @Mock private JavaMailSender mailSender;

  @Mock private MimeMessage mimeMessage;

  @InjectMocks private EmailSenderService emailSenderService;

  @BeforeEach
  void setUp() {
    emailSenderService.senderAddress = "teste@orienta.com";
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
  }

  @Test
  void sendEmailShouldConfigureHelperAndInvokeMailSender() throws Exception {
    try (MockedConstruction<MimeMessageHelper> helperConstruction = mockHelper()) {
      emailSenderService.sendEmail("destino@teste.com", "Assunto", "<h1>Conteúdo</h1>");

      MimeMessageHelper helper = helperConstruction.constructed().getFirst();
      verify(helper).setTo("destino@teste.com");
      verify(helper).setSubject("Assunto");
      verify(helper).setText("<h1>Conteúdo</h1>", true);
      verify(helper).setFrom("teste@orienta.com");
      verify(mailSender).send(mimeMessage);
    }
  }

  @Test
  void sendEmailShouldThrowInternalErrorWhenMessagingFails() {
    try (MockedConstruction<MimeMessageHelper> helperConstruction =
        mockHelper(
            mock -> {
              try {
                doThrow(new MessagingException("fail")).when(mock).setTo(anyString());
              } catch (MessagingException e) {
                throw new RuntimeException(e);
              }
            })) {
      InternalErrorException exception =
          assertThrows(
              InternalErrorException.class,
              () -> emailSenderService.sendEmail("destino@teste.com", "Assunto", "conteúdo"));

      assertEquals("Erro ao enviar email", exception.getMessage());
      verify(mailSender, never()).send(any(MimeMessage.class));
    }
  }

  @Test
  void sendEmailWithAttachmentShouldAttachFileAndSend() throws Exception {
    byte[] fileData = "pdf".getBytes();
    try (MockedConstruction<MimeMessageHelper> helperConstruction = mockHelper()) {
      emailSenderService.sendEmailWithAttachment(
          "destino@teste.com", "Assunto", "<p>Conteúdo</p>", "cert.pdf", fileData);

      MimeMessageHelper helper = helperConstruction.constructed().getFirst();
      ArgumentCaptor<InputStreamSource> resourceCaptor =
          ArgumentCaptor.forClass(InputStreamSource.class);
      verify(helper).addAttachment(eq("cert.pdf"), resourceCaptor.capture());
      assertTrue(resourceCaptor.getValue() instanceof ByteArrayResource);
      assertArrayEquals(fileData, ((ByteArrayResource) resourceCaptor.getValue()).getByteArray());
      verify(mailSender).send(mimeMessage);
    }
  }

  @Test
  void sendEmailWithAttachmentShouldThrowInternalErrorWhenMessagingFails() {
    try (MockedConstruction<MimeMessageHelper> helperConstruction =
        mockHelper(
            mock -> {
              try {
                doThrow(new MessagingException("fail"))
                    .when(mock)
                    .addAttachment(anyString(), any(InputStreamSource.class));
              } catch (MessagingException e) {
                throw new RuntimeException(e);
              }
            })) {
      InternalErrorException exception =
          assertThrows(
              InternalErrorException.class,
              () ->
                  emailSenderService.sendEmailWithAttachment(
                      "destino@teste.com", "Assunto", "conteúdo", "cert.pdf", new byte[0]));

      assertEquals("Erro ao enviar e-mail com anexo", exception.getMessage());
      verify(mailSender, never()).send(any(MimeMessage.class));
    }
  }

  private MockedConstruction<MimeMessageHelper> mockHelper() {
    return mockHelper(helper -> {});
  }

  private MockedConstruction<MimeMessageHelper> mockHelper(
      Consumer<MimeMessageHelper> configurator) {
    return mockConstruction(MimeMessageHelper.class, (mock, context) -> configurator.accept(mock));
  }
}
