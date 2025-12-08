package umc.pfc.orientamais.application.port.output.calendar;

import java.util.List;
import umc.pfc.orientamais.domain.model.clazz.Lesson;

public interface CalendarPort {
  String createEvent(Lesson lesson, List<String> attendees);

  void sendInviteToMentored(Lesson lesson, String mentoredEmail);

  void cancelEvent(Lesson lesson, List<String> mentoredEmails);
}
