package umc.pfc.orientamais.application.service.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class EmailSenderServiceTest {

    private JavaMailSender mailSender;
    private EmailSenderService emailSenderService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderService = new EmailSenderService(mailSender);
        emailSenderService.senderAddress = "teste@orienta.com";
    }

    @Test
    void shouldSendEmailSuccessfully() throws Exception {
        emailSenderService.sendEmail("destino@teste.com", "Assunto Teste", "<h1>Conteúdo</h1>");

        verify(mailSender).send(mimeMessage);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());

        MimeMessage sentMessage = captor.getValue();
        assertNotNull(sentMessage);
    }
}
