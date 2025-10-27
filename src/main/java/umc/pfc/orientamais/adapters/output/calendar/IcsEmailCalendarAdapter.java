package umc.pfc.orientamais.adapters.output.calendar;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.application.port.output.calendar.CalendarPort;
import umc.pfc.orientamais.application.service.utils.IcsBuilder;
import umc.pfc.orientamais.domain.model.Lesson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IcsEmailCalendarAdapter implements CalendarPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderAddress;

    @Override
    public String createEvent(Lesson lesson, List<String> attendees) {
        try {
            String uid = IcsBuilder.generateUid(lesson);
            String ics = IcsBuilder.buildIcsEvent(lesson, attendees, senderAddress, uid);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(attendees.toArray(new String[0]));
            helper.setSubject("Convite: " + lesson.getTitle());
            helper.setText(buildHtmlBody(lesson), true);
            helper.setFrom(senderAddress, "Orientamais");

            helper.addAttachment("invite.ics", new DataSource() {
                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream(ics.getBytes(StandardCharsets.UTF_8));
                }

                @Override
                public OutputStream getOutputStream() {
                    throw new UnsupportedOperationException("Read-only");
                }

                @Override
                public String getContentType() {
                    return "text/calendar; method=REQUEST; charset=UTF-8";
                }

                @Override
                public String getName() {
                    return "invite.ics";
                }
            });

            mailSender.send(message);
            return uid;
        } catch (Exception ex) {
            throw new RuntimeException("Falha ao enviar convite por e-mail", ex);
        }
    }

    private String buildHtmlBody(Lesson lesson) {
        return "<p>Olá,</p>" +
                "<p>Você foi convidado para a aula: <strong>" + lesson.getTitle() + "</strong></p>" +
                "<p>Data: " + lesson.getStartTime() + "</p>" +
                "<p>Descrição: " + (lesson.getDescription() != null ? lesson.getDescription() : "") + "</p>" +
                "<p><a href=\"" + lesson.getLink() + "\" target=\"_blank\" style=\"display:inline-block;padding:10px 20px;" +
                "color:#ffffff;background-color:#1a73e8;text-decoration:none;border-radius:5px;\">Entrar na Reunião</a></p>" +
                "<p>Cumprimentos,<br/>Equipe Orientamais</p>";
    }
}