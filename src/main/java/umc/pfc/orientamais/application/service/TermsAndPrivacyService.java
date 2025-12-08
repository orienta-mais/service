package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.TermsAndPrivacyRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.TermsAndPrivacyResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.TermsAndPrivacyRepository;
import umc.pfc.orientamais.application.mapper.TermsAndPrivacyMapper;
import umc.pfc.orientamais.application.port.input.TermsAndPrivacyUseCase;
import umc.pfc.orientamais.domain.exceptions.BadRequestException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.terms.TermType;
import umc.pfc.orientamais.domain.model.terms.TermsAndPrivacy;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TermsAndPrivacyService implements TermsAndPrivacyUseCase {

  private final TermsAndPrivacyRepository repository;
  private final TermsAndPrivacyMapper mapper;

  @Override
  public TermsAndPrivacyResponse getActiveTerm(TermType type) {
    validateTermType(type);

    TermsAndPrivacy activeTerm =
        repository
            .findByTypeAndIsActive(type, true)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Nenhum termo ativo encontrado para o tipo: " + type.getDisplayName()));

    return mapper.entityToResponse(activeTerm);
  }

  @Override
  public String getActiveTermHtml(TermType type) {
    log.info("Buscando HTML do termo ativo do tipo: {}", type);

    validateTermType(type);

    TermsAndPrivacy activeTerm =
        repository
            .findByTypeAndIsActive(type, true)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Nenhum termo ativo encontrado para o tipo: " + type.getDisplayName()));

    return activeTerm.getContent();
  }

  @Override
  public List<TermsAndPrivacyResponse> getAllVersions(TermType type) {
    validateTermType(type);
    List<TermsAndPrivacy> versions = repository.findAllByTypeOrderByVersionDesc(type);
    return mapper.entitiesToSummaryResponses(versions);
  }

  @Override
  public TermsAndPrivacyResponse getTermByVersion(TermType type, Integer version) {
    validateTermType(type);
    validateVersion(version);

    TermsAndPrivacy term =
        repository
            .findByTypeAndVersion(type, version)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        String.format(
                            "Termo não encontrado para tipo %s e versão %d",
                            type.getDisplayName(), version)));

    return mapper.entityToResponse(term);
  }

  @Override
  public TermsAndPrivacyResponse createNewVersion(TermsAndPrivacyRequest request) {
    validateRequest(request);

    try {
      deactivateCurrentActiveTerm(request.type());
      Integer nextVersion = calculateNextVersion(request.type());

      TermsAndPrivacy newTerm = mapper.requestToEntity(request, nextVersion);
      TermsAndPrivacy savedTerm = repository.save(newTerm);

      return mapper.entityToResponse(savedTerm);

    } catch (Exception ex) {
      log.error("Erro ao criar nova versão do termo: {}", ex.getMessage(), ex);
      throw new BadRequestException("Erro ao criar nova versão do termo: " + ex.getMessage());
    }
  }

  @Override
  public TermsAndPrivacyResponse updateTerm(TermsAndPrivacyRequest request) {
    log.info("Atualizando termo tipo: {}", request.type());

    validateRequest(request);

    return createNewVersion(request);
  }

  @Override
  public TermsAndPrivacyResponse activateVersion(TermType type, Integer version) {
    log.info("Ativando versão {} do termo tipo: {}", version, type);

    validateTermType(type);
    validateVersion(version);

    try {
      TermsAndPrivacy termToActivate =
          repository
              .findByTypeAndVersion(type, version)
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          String.format(
                              "Termo não encontrado para tipo %s e versão %d",
                              type.getDisplayName(), version)));

      if (Boolean.TRUE.equals(termToActivate.getIsActive())) {
        log.warn("Versão {} do tipo {} já está ativa", version, type);
        return mapper.entityToResponse(termToActivate);
      }

      deactivateCurrentActiveTerm(type);

      termToActivate.setIsActive(true);
      TermsAndPrivacy activatedTerm = repository.save(termToActivate);

      log.info("Versão {} do tipo {} ativada com sucesso", version, type);

      return mapper.entityToResponse(activatedTerm);

    } catch (NotFoundException ex) {
      throw ex;
    } catch (Exception ex) {
      log.error("Erro ao ativar versão do termo: {}", ex.getMessage(), ex);
      throw new BadRequestException("Erro ao ativar versão do termo: " + ex.getMessage());
    }
  }

  @Override
  public List<TermsAndPrivacyResponse> getAllActiveTerms() {

    List<TermsAndPrivacy> activeTerms = repository.findByIsActive(true);

    if (activeTerms.isEmpty()) {
      log.warn("Nenhum termo ativo encontrado no sistema");
    }

    return mapper.entitiesToResponses(activeTerms);
  }

  @Override
  public GenericModelResponse deleteVersion(TermType type, Integer version) {
    log.info("Deletando versão {} do termo tipo: {}", version, type);

    validateTermType(type);
    validateVersion(version);

    try {
      TermsAndPrivacy term =
          repository
              .findByTypeAndVersion(type, version)
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          String.format(
                              "Termo não encontrado para tipo %s e versão %d",
                              type.getDisplayName(), version)));

      if (Boolean.TRUE.equals(term.getIsActive())) {
        throw new BadRequestException(
            "Não é possível deletar uma versão ativa. Desative-a primeiro.");
      }

      repository.delete(term);

      log.info("Versão {} do tipo {} deletada com sucesso", version, type);

      return new GenericModelResponse(
          "TERM_DELETED",
          String.format(
              "Versão %d do termo %s deletada com sucesso", version, type.getDisplayName()));

    } catch (NotFoundException | BadRequestException ex) {
      throw ex;
    } catch (Exception ex) {
      log.error("Erro ao deletar versão do termo: {}", ex.getMessage(), ex);
      throw new BadRequestException("Erro ao deletar versão do termo: " + ex.getMessage());
    }
  }

  private void deactivateCurrentActiveTerm(TermType type) {
    repository
        .findByTypeAndIsActive(type, true)
        .ifPresent(
            activeTerm -> {
              log.info("Desativando versão {} do tipo {}", activeTerm.getVersion(), type);
              activeTerm.setIsActive(false);
              repository.save(activeTerm);
            });
  }

  private Integer calculateNextVersion(TermType type) {
    return repository.findAllByTypeOrderByVersionDesc(type).stream()
        .findFirst()
        .map(term -> term.getVersion() + 1)
        .orElse(1);
  }

  private void validateTermType(TermType type) {
    if (type == null || (!type.equals(TermType.TERMS) && !type.equals(TermType.PRIVACY))) {
      throw new BadRequestException("Tipo de termo inválido, deve ser TERMS ou PRIVACY");
    }
  }

  private void validateVersion(Integer version) {
    if (version == null) {
      throw new BadRequestException("Versão não pode ser nula");
    }
    if (version < 1) {
      throw new BadRequestException("Versão deve ser maior que zero");
    }
  }

  private void validateRequest(TermsAndPrivacyRequest request) {
    if (request == null) {
      throw new BadRequestException("Request não pode ser nulo");
    }
    validateTermType(request.type());
    if (request.content() == null || request.content().trim().isEmpty()) {
      throw new BadRequestException("Conteúdo não pode ser vazio");
    }
  }
}
