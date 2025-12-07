package umc.pfc.orientamais.adapters.output.persistence.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

public interface MentoredRepository extends JpaRepository<Mentored, UUID> {
  Optional<Mentored> findByUserId(UUID id);

  @Query(
      value = """
            select count(*) from mentored m;
            """,
      nativeQuery = true)
  Integer countMentoreds();

  @Query(
      value =
          """
            SELECT state, COUNT(*) AS total
            FROM (
                SELECT state FROM mentored
                UNION ALL
                SELECT state FROM mentor
            ) AS combined
            GROUP BY state
            ORDER BY state;
            """, nativeQuery = true)
    List<Object[]> countByState();

    @Modifying
    @Transactional
    @Query(value = """
            delete from mentored where id = :id;
            """, nativeQuery = true)
    void deleteMentored(UUID id);
}
