package umc.pfc.orientamais.adapters.input.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.domain.model.terms.TermType;

@Component
public class TermTypeConverter implements Converter<String, TermType> {

  @Override
  public TermType convert(@NonNull String source) {
    if (source.trim().isEmpty()) {
      throw new IllegalArgumentException("Tipo de termo não pode ser vazio");
    }

    String normalized = source.trim().toUpperCase();

    return switch (normalized) {
      case "PRIVACY" -> TermType.PRIVACY;
      case "TERMS" -> TermType.TERMS;
      default ->
          throw new IllegalArgumentException(
              "Tipo de termo inválido: '"
                  + source
                  + "'. Use 'TERMS' ou 'PRIVACY' (maiúsculas ou minúsculas)");
    };
  }
}
