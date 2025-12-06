package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

public interface ValidateEmailUseCase {
    void validateAndSendLink(EmailModelRequest request, AuthUserRole role);
}
