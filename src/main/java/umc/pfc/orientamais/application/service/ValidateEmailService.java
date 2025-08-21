package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.response.ValidateEmailModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;
import umc.pfc.orientamais.domain.model.Email;

@Service
@Transactional
@AllArgsConstructor
public class ValidateEmailService implements ValidateEmailUseCase {

    private final AuthUserRepository authUserRepository;

    @Override
    public ValidateEmailModelResponse validateEmail(Email emailModelRequest) {
        if (authUserRepository.existsByEmail(emailModelRequest.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        return new ValidateEmailModelResponse("EMAIL_VALIDATED", true);
    }
}
