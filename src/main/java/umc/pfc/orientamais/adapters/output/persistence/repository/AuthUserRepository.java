package umc.pfc.orientamais.adapters.output.persistence.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.domain.model.auth.AuthUser;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);
    @Modifying
    @Transactional
    @Query(value = """
            delete from auth_user
            WHERE id = :id;
            """, nativeQuery = true)
    void anonymizeAuthUserData(UUID id);
}
