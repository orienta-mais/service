package umc.pfc.orientamais.application.port.output.sanitization;

public interface SanitizationPort {

  String sanitizeHtml(String input);

  boolean isSecure(String input, boolean allowHtml);
}

