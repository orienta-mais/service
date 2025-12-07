package umc.pfc.orientamais.application.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.input.rest.dto.request.TermsAndPrivacyRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.TermsAndPrivacyResponse;
import umc.pfc.orientamais.domain.model.terms.TermsAndPrivacy;
import umc.pfc.orientamais.domain.utils.InputSanitizer;

@Component
public class TermsAndPrivacyMapper {

  public TermsAndPrivacy requestToEntity(TermsAndPrivacyRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Request não pode ser nulo");
    }

    String sanitizedContent = InputSanitizer.sanitize(request.content());

    TermsAndPrivacy entity = new TermsAndPrivacy();
    entity.setType(request.type());
    entity.setContent(sanitizedContent);
    entity.setVersion(1);
    entity.setIsActive(true);

    return entity;
  }

  public TermsAndPrivacy requestToEntity(TermsAndPrivacyRequest request, Integer version) {
    if (request == null) {
      throw new IllegalArgumentException("Request não pode ser nulo");
    }
    if (version == null || version < 1) {
      throw new IllegalArgumentException("Versão deve ser maior que zero");
    }

    String sanitizedContent = InputSanitizer.sanitize(request.content());

    TermsAndPrivacy entity = new TermsAndPrivacy();
    entity.setType(request.type());
    entity.setContent(sanitizedContent);
    entity.setVersion(version);
    entity.setIsActive(true);

    return entity;
  }

  public TermsAndPrivacyResponse entityToResponse(TermsAndPrivacy entity) {
    if (entity == null) {
      return null;
    }

    return new TermsAndPrivacyResponse(
        entity.getId(),
        entity.getType(),
        entity.getContent(),
        entity.getVersion(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getIsActive());
  }

  public TermsAndPrivacyResponse entityToSummaryResponse(TermsAndPrivacy entity) {
    if (entity == null) {
      return null;
    }

    String contentSummary =
        entity.getContent() != null && entity.getContent().length() > 100
            ? entity.getContent().substring(0, 100) + "..."
            : entity.getContent();

    return new TermsAndPrivacyResponse(
        entity.getId(),
        entity.getType(),
        contentSummary,
        entity.getVersion(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getIsActive());
  }

  public List<TermsAndPrivacyResponse> entitiesToResponses(List<TermsAndPrivacy> entities) {
    if (entities == null) {
      return List.of();
    }

    return entities.stream().map(this::entityToResponse).toList();
  }

  public List<TermsAndPrivacyResponse> entitiesToSummaryResponses(List<TermsAndPrivacy> entities) {
    if (entities == null) {
      return List.of();
    }

    return entities.stream().map(this::entityToSummaryResponse).toList();
  }
}

