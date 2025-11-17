package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.PresenceCodeModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;

import java.util.UUID;

public interface CertificateUseCase {
    GenericModelResponse validatePresenceAndGenerateCertificate(UUID lessonId, PresenceCodeModelRequest request);
    GenericModelResponse regenerateCertificate(UUID lessonId);
}
