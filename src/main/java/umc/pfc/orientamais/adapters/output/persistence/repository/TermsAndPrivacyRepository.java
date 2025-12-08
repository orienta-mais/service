package umc.pfc.orientamais.adapters.output.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.domain.model.terms.TermType;
import umc.pfc.orientamais.domain.model.terms.TermsAndPrivacy;

@Repository
public interface TermsAndPrivacyRepository extends JpaRepository<TermsAndPrivacy, UUID> {

  Optional<TermsAndPrivacy> findByTypeAndIsActive(TermType type, Boolean isActive);

  @Query("SELECT t FROM TermsAndPrivacy t WHERE t.type = :type ORDER BY t.version DESC")
  List<TermsAndPrivacy> findAllByTypeOrderByVersionDesc(TermType type);

  List<TermsAndPrivacy> findByIsActive(Boolean isActive);

  Optional<TermsAndPrivacy> findByTypeAndVersion(TermType type, Integer version);

  boolean existsByTypeAndIsActive(TermType type, Boolean isActive);
}
