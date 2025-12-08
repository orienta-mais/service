package umc.pfc.orientamais.adapters.output.calendar;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.application.port.output.calendar.CalendarPort;
import umc.pfc.orientamais.application.service.utils.IcsBuilder;
import umc.pfc.orientamais.domain.model.clazz.Lesson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class IcsEmailCalendarAdapter implements CalendarPort {

  private static final ZoneId ZONE_SAO_PAULO = ZoneId.of("America/Sao_Paulo");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
    .ofPattern("dd/MM/yyyy")
    .withLocale(Locale.forLanguageTag("pt-BR"));
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
    .ofPattern("HH:mm")
    .withLocale(Locale.forLanguageTag("pt-BR"));
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

      helper.addAttachment(
        "invite.ics",
        new DataSource() {
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

  @Override
  public void sendInviteToMentored(Lesson lesson, String mentoredEmail) {
    try {
      String uid = IcsBuilder.generateUid(lesson);
      String ics = IcsBuilder.buildIcsEvent(lesson, List.of(mentoredEmail), senderAddress, uid);

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(mentoredEmail);
      helper.setSubject("Convite para aula: " + lesson.getTitle());
      helper.setText(buildHtmlBody(lesson), true);
      helper.setFrom(senderAddress, "Orientamais");

      helper.addAttachment(
        "invite.ics",
        new DataSource() {
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
    } catch (Exception ex) {
      throw new RuntimeException("Falha ao enviar convite por e-mail para o mentorado", ex);
    }
  }

  @Override
  public void cancelEvent(Lesson lesson, List<String> mentoredEmails) {
    if (mentoredEmails == null || mentoredEmails.isEmpty()) {
      return;
    }

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    String subject = "Aula Cancelada - " + lesson.getTitle();
    String text = buildLessonCanceledEmailText(lesson, dateFormatter, timeFormatter);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setSubject(subject);
    message.setText(text);
    mentoredEmails.forEach(
        email -> {
          try {
            message.setTo(email);
            mailSender.send(message);
          } catch (Exception e) {
            log.error("Erro ao enviar email de cancelamento para {}: {}", email, e.getMessage());
          }
        });
  }

  private String buildHtmlBody(Lesson lesson) {
    LocalDateTime startTimeBRT = lesson.getStartTime().minusHours(3);

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    String subject = "Aula Cancelada - " + lesson.getTitle();
    String text = buildLessonCanceledEmailText(lesson, dateFormatter, timeFormatter);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setSubject(subject);
    message.setText(text);
    mentoredEmails.forEach(
      email -> {
        try {
          message.setTo(email);
          mailSender.send(message);
        } catch (Exception e) {
          log.error("Erro ao enviar email de cancelamento para {}: {}", email, e.getMessage());
        }
      });
  }

  private String buildHtmlBody(Lesson lesson) {

    ZonedDateTime zonedStart = lesson.getStartTime()
      .atZone(ZoneId.of("UTC"))
      .withZoneSameInstant(ZONE_SAO_PAULO);

    String dateFormatted = zonedStart.toLocalDate().format(DATE_FORMATTER);
    String timeFormatted = zonedStart.toLocalTime().format(TIME_FORMATTER);

    return "<p>Olá,</p>"
      + "<p>Você foi convidado para a aula: <strong>" + lesson.getTitle() + "</strong></p>"
      + "<p>Data: " + dateFormatted + " - " + timeFormatted + "</p>"
      + "<p>Descrição: " + (lesson.getDescription() != null ? lesson.getDescription() : "") + "</p>"
      + "<p><a href=\"" + lesson.getLink() + "\" target=\"_blank\" "
      + "style=\"display:inline-block;padding:10px 20px;color:#ffffff;"
      + "background-color:#1a73e8;text-decoration:none;border-radius:5px;\">Entrar na Reunião</a></p>"
      + "<p>Cumprimentos,<br/>Equipe Orientamais</p>";
  }

  private String buildLessonCanceledEmailText(
    Lesson lesson,) {
      ZonedDateTime zonedStart = lesson.getStartTime()
      .atZone(ZoneId.of("UTC"))
      .withZoneSameInstant(ZONE_SAO_PAULO);

    String dateFormatted = zonedStart.toLocalDate().format(DATE_FORMATTER);
    String timeFormatted = zonedStart.toLocalTime().format(TIME_FORMATTER);

    return String.format(
      """
        Olá,

        Informamos que a aula "%s" foi cancelada.

        Detalhes da aula cancelada:
        - Data: %s
        - Horário: %s às %s

        Pedimos desculpas pelo inconveniente.

        Atenciosamente,
        Equipe Orienta+
        """,

      + "<p>Data: " + dateFormatted
      + " - " + zonedStart.toLocalTime().format(TIME_FORMATTER) + "</p>"
      + "<p>Descrição: " + (lesson.getDescription() != null ? lesson.getDescription() : "") + "</p>"
      + "<p><a href=\"" + lesson.getLink()
      + "\" target=\"_blank\" style=\"display:inline-block;padding:10px 20px;color:#ffffff;"
      + "background-color:#1a73e8;text-decoration:none;border-radius:5px;\">Entrar na Reunião</a></p>"
      + "<p>Cumprimentos,<br/>Equipe OrientaMais</p>";
  }
}
