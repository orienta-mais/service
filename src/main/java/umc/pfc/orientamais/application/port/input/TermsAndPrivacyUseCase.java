package umc.pfc.orientamais.application.port.input;

import java.util.List;
import umc.pfc.orientamais.adapters.input.rest.dto.request.TermsAndPrivacyRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.TermsAndPrivacyResponse;
import umc.pfc.orientamais.domain.model.terms.TermType;

public interface TermsAndPrivacyUseCase {
  TermsAndPrivacyResponse getActiveTerm(TermType type);

  String getActiveTermHtml(TermType type);

  List<TermsAndPrivacyResponse> getAllVersions(TermType type);

  TermsAndPrivacyResponse getTermByVersion(TermType type, Integer version);

  TermsAndPrivacyResponse createNewVersion(TermsAndPrivacyRequest request);

  TermsAndPrivacyResponse updateTerm(TermsAndPrivacyRequest request);

  TermsAndPrivacyResponse activateVersion(TermType type, Integer version);

  List<TermsAndPrivacyResponse> getAllActiveTerms();

  GenericModelResponse deleteVersion(TermType type, Integer version);
}
