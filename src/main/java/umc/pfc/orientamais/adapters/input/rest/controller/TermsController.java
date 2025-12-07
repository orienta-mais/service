package umc.pfc.orientamais.adapters.input.rest.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.TermsAndPrivacyRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.TermsAndPrivacyResponse;
import umc.pfc.orientamais.application.port.input.TermsAndPrivacyUseCase;
import umc.pfc.orientamais.domain.model.terms.TermType;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TermsController {

  private final TermsAndPrivacyUseCase termsAndPrivacyUseCase;

  @GetMapping("/terms/active/{type}")
  public ResponseEntity<TermsAndPrivacyResponse> getActiveTerm(
    @PathVariable
    TermType type) {

    TermsAndPrivacyResponse response = termsAndPrivacyUseCase.getActiveTerm(type);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/terms/active/{type}/html")
  public ResponseEntity<String> getActiveTermHtml(
    @PathVariable
    TermType type) {

    String htmlContent = termsAndPrivacyUseCase.getActiveTermHtml(type);
    return ResponseEntity.ok()
      .header("Content-Type", "text/html; charset=UTF-8")
      .body(htmlContent);
  }

  @GetMapping("/terms/versions/{type}")
  public ResponseEntity<List<TermsAndPrivacyResponse>> getAllVersions(
    @PathVariable TermType type) {

    List<TermsAndPrivacyResponse> response = termsAndPrivacyUseCase.getAllVersions(type);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/terms/version/{type}/{version}")
  public ResponseEntity<TermsAndPrivacyResponse> getTermByVersion(
    @PathVariable TermType type,
    @PathVariable Integer version) {

    TermsAndPrivacyResponse response = termsAndPrivacyUseCase.getTermByVersion(type, version);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/terms/active")
  public ResponseEntity<List<TermsAndPrivacyResponse>> getAllActiveTerms() {
    List<TermsAndPrivacyResponse> response = termsAndPrivacyUseCase.getAllActiveTerms();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/admin/terms/policy")
  public ResponseEntity<TermsAndPrivacyResponse> createNewVersion(
    @Valid @RequestBody TermsAndPrivacyRequest request) {

    TermsAndPrivacyResponse response = termsAndPrivacyUseCase.createNewVersion(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/admin/terms/policy")
  public ResponseEntity<TermsAndPrivacyResponse> updateTerm(
    @Valid @RequestBody TermsAndPrivacyRequest request) {

    TermsAndPrivacyResponse response = termsAndPrivacyUseCase.updateTerm(request);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/admin/terms/policy/activate/{type}/{version}")
  public ResponseEntity<TermsAndPrivacyResponse> activateVersion(
    @Parameter(description = "Type of term (TERMS/terms or PRIVACY/privacy - case insensitive)") @PathVariable TermType type,
    @Parameter(description = "Version to activate") @PathVariable Integer version) {

    TermsAndPrivacyResponse response = termsAndPrivacyUseCase.activateVersion(type, version);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/admin/terms/policy/{type}/{version}")
  public ResponseEntity<GenericModelResponse> deleteVersion(
    @PathVariable TermType type,
    @PathVariable Integer version) {

    GenericModelResponse response = termsAndPrivacyUseCase.deleteVersion(type, version);
    return ResponseEntity.ok(response);
  }
}
