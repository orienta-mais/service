package umc.pfc.orientamais.application.port.input;

import java.util.UUID;
import umc.pfc.orientamais.adapters.input.rest.dto.request.PresenceCodeModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;

public interface CertificateUseCase {
  GenericModelResponse validatePresenceAndGenerateCertificate(
      UUID lessonId, PresenceCodeModelRequest request);

  GenericModelResponse regenerateCertificate(UUID lessonId);
}
