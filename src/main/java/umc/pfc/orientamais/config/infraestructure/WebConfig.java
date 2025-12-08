package umc.pfc.orientamais.config.infraestructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import umc.pfc.orientamais.adapters.input.rest.converter.TermTypeConverter;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final TermTypeConverter termTypeConverter;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(termTypeConverter);
  }
}
