package umc.pfc.orientamais.application.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String senderAddress;

    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom(senderAddress);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new InternalErrorException("Erro ao enviar email");
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String content, String filename, byte[] fileData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom(senderAddress);
            helper.addAttachment(filename, new ByteArrayResource(fileData));
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new InternalErrorException("Erro ao enviar e-mail com anexo");
        }
    }

}
