package umc.pfc.orientamais.adapters.input.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import umc.pfc.orientamais.domain.model.terms.TermType;

public record TermsAndPrivacyResponse(
    UUID id,
    TermType type,
    String content,
    Integer version,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean isActive) {}
