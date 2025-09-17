package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;
import umc.pfc.orientamais.application.service.email.EmailSenderService;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.model.RegistrationToken;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidateEmailService implements ValidateEmailUseCase {

    private final AuthUserRepository authUserRepository;
    private final RegistrationTokenRepository tokenRepository;
    private final EmailSenderService emailSender;

    @Override
    public void validateAndSendLink(EmailModelRequest request) {
        if (authUserRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email já cadastrado: " + request.email());
        }

        String token = UUID.randomUUID().toString();
        RegistrationToken registrationToken = new RegistrationToken(
                request.email(),
                token,
                LocalDateTime.now().plusHours(24)
        );

        tokenRepository.save(registrationToken);

        String link = "http://frontend-orienta.com/register?token=" + token;
        emailSender.sendEmail(request.email(), "Complete seu cadastro", "Clique aqui: " + link);
    }
}