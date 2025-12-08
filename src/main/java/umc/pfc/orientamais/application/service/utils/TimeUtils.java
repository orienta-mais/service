package umc.pfc.orientamais.application.service.utils;

import java.time.*;

public class TimeUtils {

  private TimeUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static Instant nowUtc() {
    return Instant.now();
  }

  public static LocalDateTime nowLocalDateTimeUtc() {
    return LocalDateTime.now(ZoneOffset.UTC);
  }

  public static Instant toInstantUtc(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    return localDateTime.toInstant(ZoneOffset.UTC);
  }

  public static LocalDateTime toLocalDateTimeUtc(Instant instant) {
    if (instant == null) {
      return null;
    }
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

  public static LocalDateTime toLocalDateTimeUtc(LocalDate date, LocalTime time) {
    if (date == null || time == null) {
      return null;
    }
    return LocalDateTime.of(date, time);
  }

  public static ZonedDateTime toUtc(ZonedDateTime zonedDateTime) {
    if (zonedDateTime == null) {
      return null;
    }
    return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
  }
}
