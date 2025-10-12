package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;
import umc.pfc.orientamais.application.service.email.EmailSenderService;
import umc.pfc.orientamais.application.service.utils.EmailTemplateBuilder;
import umc.pfc.orientamais.application.service.utils.RegistrationTokenFactory;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.model.AuthUserRole;
import umc.pfc.orientamais.domain.model.RegistrationToken;

@Service
@RequiredArgsConstructor
public class ValidateEmailService implements ValidateEmailUseCase {

    private final AuthUserRepository authUserRepository;
    private final RegistrationTokenRepository tokenRepository;
    private final EmailSenderService emailSender;
    private final RegistrationTokenFactory tokenFactory;
    private final EmailTemplateBuilder templateProvider;

    @Value("${app.registration.url}")
    private String registerUrl;

    @Override
    public void validateAndSendLink(EmailModelRequest request, AuthUserRole role) {
        if (authUserRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email já cadastrado: " + request.email());
        }

        RegistrationToken token = tokenFactory.create(request.email(), role);
        try {
            tokenRepository.save(token);
        } catch (Exception e) {
            RegistrationToken oldToken = tokenRepository.findByEmail(request.email()).orElse(token);
            tokenRepository.deleteByEmail(oldToken.getEmail());
            tokenRepository.save(token);
        }

        String link = UriComponentsBuilder
                .fromUriString(registerUrl)
                .path(role.toString().toLowerCase())
                .queryParam("token", token.getToken())
                .queryParam("email", token.getEmail())
                .toUriString();

        String htmlContent = templateProvider.buildMentorRegisterEmail(link);

        try {
            emailSender.sendEmail(request.email(), "Complete seu cadastro", htmlContent);
        } catch (Exception e) {
            tokenRepository.delete(token);
            throw new InternalErrorException("Erro ao enviar e-mail de validação");
        }
    }
}
