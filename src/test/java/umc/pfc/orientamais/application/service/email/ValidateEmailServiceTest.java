package umc.pfc.orientamais.application.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.application.service.utils.EmailTemplateBuilder;
import umc.pfc.orientamais.application.service.utils.RegistrationTokenFactory;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.auth.RegistrationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateEmailServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private RegistrationTokenRepository tokenRepository;

    @Mock
    private EmailSenderService emailSender;

    @Mock
    private RegistrationTokenFactory tokenFactory;

    @Mock
    private EmailTemplateBuilder templateProvider;

    @InjectMocks
    private ValidateEmailService service;

    private EmailModelRequest request;
    private RegistrationToken token;

    @BeforeEach
    void setUp() {
        request = new EmailModelRequest("teste@exemplo.com");
        token = new RegistrationToken(null, "teste@exemplo.com", "token123", null, AuthUserRole.MENTOR);
        ReflectionTestUtils.setField(service, "registerUrl", "http://localhost:8080/register/");
    }

    @Test
    void validateAndSendLinkShouldPersistTokenAndSendEmail() {
        when(authUserRepository.existsByEmail(request.email())).thenReturn(false);
        when(tokenFactory.create(request.email(), AuthUserRole.MENTOR)).thenReturn(token);
        when(templateProvider.buildMentorRegisterEmail(anyString())).thenReturn("<html>link</html>");

        service.validateAndSendLink(request, AuthUserRole.MENTOR);

        verify(tokenRepository).save(token);
        verify(emailSender).sendEmail(eq(request.email()), eq("Complete seu cadastro"), eq("<html>link</html>"));
    }

    @Test
    void validateAndSendLinkShouldThrowWhenEmailAlreadyExists() {
        when(authUserRepository.existsByEmail(request.email())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> service.validateAndSendLink(request, AuthUserRole.MENTOR));

        assertEquals("Email já cadastrado: teste@exemplo.com", exception.getMessage());
        verifyNoInteractions(tokenRepository, emailSender, tokenFactory, templateProvider);
    }

    @Test
    void validateAndSendLinkShouldRetryWhenFirstSaveFails() {
        when(authUserRepository.existsByEmail(request.email())).thenReturn(false);
        when(tokenFactory.create(request.email(), AuthUserRole.MENTOR)).thenReturn(token);
        when(tokenRepository.save(token))
                .thenThrow(new RuntimeException("DB error"))
                .thenReturn(token);
        RegistrationToken oldToken = new RegistrationToken(null, request.email(), "old", null, AuthUserRole.MENTOR);
        when(tokenRepository.findByEmail(request.email())).thenReturn(Optional.of(oldToken));
        when(templateProvider.buildMentorRegisterEmail(anyString())).thenReturn("<html>link</html>");

        service.validateAndSendLink(request, AuthUserRole.MENTOR);

        verify(tokenRepository).deleteByEmail(request.email());
        verify(tokenRepository, times(2)).save(token);
        verify(emailSender).sendEmail(eq(request.email()), eq("Complete seu cadastro"), anyString());
    }

    @Test
    void validateAndSendLinkShouldDeleteTokenAndThrowWhenEmailSenderFails() {
        when(authUserRepository.existsByEmail(request.email())).thenReturn(false);
        when(tokenFactory.create(request.email(), AuthUserRole.MENTOR)).thenReturn(token);
        when(templateProvider.buildMentorRegisterEmail(anyString())).thenReturn("<html>link</html>");
        doThrow(new RuntimeException("smtp down")).when(emailSender).sendEmail(anyString(), anyString(), anyString());

        InternalErrorException exception = assertThrows(InternalErrorException.class,
                () -> service.validateAndSendLink(request, AuthUserRole.MENTOR));

        assertEquals("Erro ao enviar e-mail de validação", exception.getMessage());
        verify(tokenRepository).delete(token);
    }

    @Test
    void validateAndSendLinkShouldDeleteExistingTokenWhenLookupReturnsEmpty() {
        when(authUserRepository.existsByEmail(request.email())).thenReturn(false);
        when(tokenFactory.create(request.email(), AuthUserRole.MENTOR)).thenReturn(token);
        when(tokenRepository.save(token))
                .thenThrow(new RuntimeException("DB error"))
                .thenReturn(token);
        when(tokenRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(templateProvider.buildMentorRegisterEmail(anyString())).thenReturn("<html>link</html>");

        service.validateAndSendLink(request, AuthUserRole.MENTOR);

        verify(tokenRepository).deleteByEmail(request.email());
        verify(tokenRepository, times(2)).save(token);
    }
}

