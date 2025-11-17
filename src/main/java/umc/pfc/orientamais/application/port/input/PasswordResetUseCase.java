package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.ChangePasswordModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ResetPasswordModelRequest;

public interface PasswordResetUseCase {
    void requestPasswordReset(EmailModelRequest request);

    void resetPassword(ResetPasswordModelRequest request);

    void changePassword(ChangePasswordModelRequest request);
}
