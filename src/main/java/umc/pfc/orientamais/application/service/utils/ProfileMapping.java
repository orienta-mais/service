package umc.pfc.orientamais.application.service.utils;

import java.util.function.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.pfc.orientamais.domain.model.auth.Profile;

public record ProfileMapping<T extends Profile>(
    JpaRepository<T, ?> repository, Supplier<T> factory) {}
