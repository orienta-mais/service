package umc.pfc.orientamais.application.service.utils;

import umc.pfc.orientamais.domain.model.clazz.Lesson;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class IcsBuilder {

    private static final DateTimeFormatter ICS_DT_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    public static String buildIcsEvent(Lesson lesson, List<String> attendeesEmails, String organizerEmail, String uid) {

        var startUtc = lesson.getStartTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        var endUtc = lesson.getEndTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        String meetLink = lesson.getLink();

        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("PRODID:-//Orientamais//EN\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("CALSCALE:GREGORIAN\r\n");
        sb.append("METHOD:REQUEST\r\n");
        sb.append("BEGIN:VEVENT\r\n");
        sb.append("UID:").append(uid).append("\r\n");
        sb.append("DTSTAMP:").append(ICS_DT_FORMAT.format(java.time.Instant.now().atZone(ZoneId.of("UTC")))).append("\r\n");
        sb.append("DTSTART:").append(ICS_DT_FORMAT.format(startUtc)).append("\r\n");
        sb.append("DTEND:").append(ICS_DT_FORMAT.format(endUtc)).append("\r\n");
        sb.append("SUMMARY:").append(escape(lesson.getTitle())).append("\r\n");
        sb.append("DESCRIPTION:").append(escape(lesson.getDescription()))
                .append("\\nLink da reunião: ").append(meetLink).append("\r\n");
        sb.append("LOCATION:").append(escape("Online")).append("\r\n");
        sb.append("ORGANIZER;CN=\"Orientamais\":MAILTO:").append(organizerEmail).append("\r\n");

        for (String attendee : attendeesEmails) {
            sb.append("ATTENDEE;CN=").append(attendee).append(";RSVP=TRUE:MAILTO:").append(attendee).append("\r\n");
        }

        sb.append("END:VEVENT\r\n");
        sb.append("END:VCALENDAR\r\n");

        return sb.toString();
    }

    public static String generateUid(Lesson lesson) {
        return UUID.randomUUID() + "@" + "orientamais";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\n", "\\n").replace(",", "\\,");
    }
}