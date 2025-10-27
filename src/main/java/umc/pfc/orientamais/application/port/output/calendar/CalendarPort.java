package umc.pfc.orientamais.application.port.output.calendar;

import umc.pfc.orientamais.domain.exceptions.CalendarException;
import umc.pfc.orientamais.domain.model.Lesson;
import java.util.List;

public interface CalendarPort {
    String createEvent(Lesson lesson, List<String> attendees);
    void sendInviteToMentored(Lesson lesson, String mentoredEmail);
}