package umc.pfc.orientamais.application.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IcsBuilderTest {

    private Lesson lesson;

    @BeforeEach
    void setUp() {
        Mentor mentor = new Mentor();
        mentor.setName("Mentor");

        lesson = new Lesson();
        lesson.setId(UUID.randomUUID());
        lesson.setTitle("Aula especial");
        lesson.setDescription("Descrição com quebra\n e vírgula,");
        lesson.setLink("http://meet");
        lesson.setMentor(mentor);
        lesson.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        lesson.setEndTime(LocalDateTime.of(2025, 1, 1, 11, 0));
    }

    @Test
    void buildIcsEventShouldProduceValidCalendar() {
        String ics = IcsBuilder.buildIcsEvent(lesson, List.of("attendee@test.com"), "organizer@test.com", "uid-1");

        assertTrue(ics.contains("BEGIN:VCALENDAR"));
        assertTrue(ics.contains("SUMMARY:Aula\n\\, especial"));
        assertTrue(ics.contains("DESCRIPTION:Descrição com quebra\\n e vírgula\\,\\nLink"));
        assertTrue(ics.contains("ATTENDEE;CN=attendee@test.com"));
    }

    @Test
    void generateUidShouldReturnUuidWithDomain() {
        String uid = IcsBuilder.generateUid(lesson);

        assertTrue(uid.endsWith("@orientamais"));
    }

    @Test
    void buildIcsEventShouldHandleEmptyAttendees() {
        String ics = IcsBuilder.buildIcsEvent(lesson, List.of(), "organizer@test.com", "uid-1");

        assertFalse(ics.contains("ATTENDEE"));
    }
}

