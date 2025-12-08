package umc.pfc.orientamais.domain.utils;

import java.util.Locale;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

  private static final PolicyFactory SAFE_HTML_POLICY =
      Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS).and(Sanitizers.STYLES);

  public String sanitizeAllowHtml(String raw) {
    if (raw == null) return null;
    return SAFE_HTML_POLICY.sanitize(raw);
  }

  public boolean isSafeXss(String raw, boolean allowHtml) {
    if (raw == null || raw.isEmpty()) return true;
    if (allowHtml) {
      String sanitized = sanitizeAllowHtml(raw);
      return sanitized.equals(raw);
    } else {
      String stripped = SAFE_HTML_POLICY.sanitize(raw);
      return stripped.isEmpty();
    }
  }

  public boolean isSafePathTraversal(String raw) {
    if (raw == null) return true;
    String lower = raw.toLowerCase(Locale.ROOT);
    if (lower.contains("..") && (lower.contains("/") || lower.contains("\\"))) return false;
    return !lower.contains("%2e%2e") && !lower.contains("%2f") && !lower.contains("%5c");
  }

  public boolean isSafeFromNullBytes(String raw) {
    if (raw == null) return true;
    return !raw.contains("\u0000");
  }

  public boolean isSafeFromCommandInjection(String raw) {
    if (raw == null) return true;
    String[] suspects = {";", "&", "|", "`", "$(", ">$", "2>", "||", "&&"};
    for (String s : suspects) {
      if (raw.contains(s)) return false;
    }
    return true;
  }
}
