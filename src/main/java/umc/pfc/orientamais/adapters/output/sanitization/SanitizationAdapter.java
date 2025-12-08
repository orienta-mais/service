package umc.pfc.orientamais.adapters.output.sanitization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.application.port.output.sanitization.SanitizationPort;
import umc.pfc.orientamais.domain.utils.InputSanitizer;

@Component
@RequiredArgsConstructor
public class SanitizationAdapter implements SanitizationPort {

  private final InputSanitizer sanitizer;

  @Override
  public String sanitizeHtml(String input) {
    return sanitizer.sanitizeAllowHtml(input);
  }

  @Override
  public boolean isSecure(String input, boolean allowHtml) {
    if (input == null || input.isEmpty()) {
      return true;
    }

    if (!sanitizer.isSafeXss(input, allowHtml)) {
      return false;
    }

    if (!sanitizer.isSafePathTraversal(input)) {
      return false;
    }

    if (!sanitizer.isSafeFromNullBytes(input)) {
      return false;
    }

    return sanitizer.isSafeFromCommandInjection(input);
  }
}
