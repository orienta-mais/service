package umc.pfc.orientamais.adapters.output.calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;
import umc.pfc.orientamais.application.service.utils.IcsBuilder;
import umc.pfc.orientamais.domain.model.clazz.Lesson;

@ExtendWith(MockitoExtension.class)
class IcsEmailCalendarAdapterTest {

  @InjectMocks private IcsEmailCalendarAdapter adapter;

  @Mock private JavaMailSender mailSender;

  @Mock private MimeMessage mimeMessage;

  @Mock private MimeMessageHelper mimeMessageHelper;

  private Lesson lesson;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(adapter, "senderAddress", "sender@orientamais.com");
    lesson = new Lesson();
    lesson.setId(UUID.randomUUID());
    lesson.setTitle("Aula de Teste");
    lesson.setDescription("Descrição");
    lesson.setLink("https://example.com");
    lesson.setStartTime(LocalDateTime.now());
    lesson.setEndTime(LocalDateTime.now().plusHours(1));
  }

  @Test
  void shouldCreateEventAndSendInvite() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    try (MockedStatic<IcsBuilder> icsBuilder = mockStatic(IcsBuilder.class);
        MockedConstruction<MimeMessageHelper> helperConstruction =
            mockConstruction(MimeMessageHelper.class)) {
      icsBuilder.when(() -> IcsBuilder.generateUid(lesson)).thenReturn("UID-123");
      icsBuilder
          .when(() -> IcsBuilder.buildIcsEvent(eq(lesson), anyList(), anyString(), anyString()))
          .thenReturn("BEGIN:VCALENDAR");

      String uid = adapter.createEvent(lesson, List.of("user@email.com"));

      assertEquals("UID-123", uid);
      verify(mailSender).send(mimeMessage);
      MimeMessageHelper helper = helperConstruction.constructed().get(0);
      verify(helper).setSubject("Convite: " + lesson.getTitle());
      verify(helper).addAttachment((String) eq("invite.ics"), (DataSource) any());
    }
  }

  @Test
  void shouldUseEmptyDescriptionWhenNull() throws Exception {
    lesson.setDescription(null);
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    try (MockedStatic<IcsBuilder> icsBuilder = mockStatic(IcsBuilder.class);
        MockedConstruction<MimeMessageHelper> ignored = mockConstruction(MimeMessageHelper.class)) {
      icsBuilder.when(() -> IcsBuilder.generateUid(lesson)).thenReturn("UID-456");
      icsBuilder
          .when(() -> IcsBuilder.buildIcsEvent(eq(lesson), anyList(), anyString(), anyString()))
          .thenReturn("BEGIN:VCALENDAR");

      String uid = adapter.createEvent(lesson, List.of("user@email.com"));

      assertEquals("UID-456", uid);
    }
  }

  @Test
  void shouldThrowRuntimeExceptionWhenMailSenderFails() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new RuntimeException("send error")).when(mailSender).send(mimeMessage);

    try (MockedStatic<IcsBuilder> icsBuilder = mockStatic(IcsBuilder.class);
        MockedConstruction<MimeMessageHelper> ignored = mockConstruction(MimeMessageHelper.class)) {
      icsBuilder.when(() -> IcsBuilder.generateUid(lesson)).thenReturn("UID-789");
      icsBuilder
          .when(() -> IcsBuilder.buildIcsEvent(eq(lesson), anyList(), anyString(), anyString()))
          .thenReturn("BEGIN:VCALENDAR");

      RuntimeException exception =
          assertThrows(
              RuntimeException.class, () -> adapter.createEvent(lesson, List.of("user@email.com")));

      assertEquals("Falha ao enviar convite por e-mail", exception.getMessage());
    }
  }

  @Test
  void shouldSendInviteToMentoredSuccessfully() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    try (MockedStatic<IcsBuilder> icsBuilder = mockStatic(IcsBuilder.class);
        MockedConstruction<MimeMessageHelper> helperConstruction =
            mockConstruction(MimeMessageHelper.class)) {
      icsBuilder.when(() -> IcsBuilder.generateUid(lesson)).thenReturn("UID-900");
      icsBuilder
          .when(
              () ->
                  IcsBuilder.buildIcsEvent(
                      eq(lesson), eq(List.of("mentored@email.com")), anyString(), anyString()))
          .thenReturn("BEGIN:VCALENDAR");

      adapter.sendInviteToMentored(lesson, "mentored@email.com");

      verify(mailSender).send(mimeMessage);
      MimeMessageHelper helper = helperConstruction.constructed().get(0);
      verify(helper).setSubject("Convite para aula: " + lesson.getTitle());
    }
  }

  @Test
  void shouldThrowRuntimeExceptionWhenSendInviteToMentoredFails() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new RuntimeException("send error")).when(mailSender).send(mimeMessage);

    try (MockedStatic<IcsBuilder> icsBuilder = mockStatic(IcsBuilder.class);
        MockedConstruction<MimeMessageHelper> ignored = mockConstruction(MimeMessageHelper.class)) {
      icsBuilder.when(() -> IcsBuilder.generateUid(lesson)).thenReturn("UID-901");
      icsBuilder
          .when(() -> IcsBuilder.buildIcsEvent(eq(lesson), anyList(), anyString(), anyString()))
          .thenReturn("BEGIN:VCALENDAR");

      RuntimeException exception =
          assertThrows(
              RuntimeException.class,
              () -> adapter.sendInviteToMentored(lesson, "mentored@email.com"));

      assertEquals("Falha ao enviar convite por e-mail para o mentorado", exception.getMessage());
    }
  }
}
