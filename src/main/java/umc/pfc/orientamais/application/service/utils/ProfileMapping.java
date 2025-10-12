package umc.pfc.orientamais.application.service.utils;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.pfc.orientamais.domain.model.Profile;

import java.util.function.Supplier;

public record ProfileMapping<T extends Profile>(
        JpaRepository<T, ?> repository,
        Supplier<T> factory
) {
}
