package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.response.ValidateEmailModelResponse;
import umc.pfc.orientamais.domain.model.Email;

public interface ValidateEmailUseCase {
    ValidateEmailModelResponse validateEmail(Email email);
}
