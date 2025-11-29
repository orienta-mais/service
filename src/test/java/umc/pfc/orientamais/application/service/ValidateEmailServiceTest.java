package umc.pfc.orientamais.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.application.service.email.EmailSenderService;
import umc.pfc.orientamais.application.service.email.ValidateEmailService;
import umc.pfc.orientamais.application.service.utils.EmailTemplateBuilder;
import umc.pfc.orientamais.application.service.utils.RegistrationTokenFactory;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.auth.RegistrationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ValidateEmailServiceTest {

    private AuthUserRepository authUserRepository;
    private RegistrationTokenRepository tokenRepository;
    private EmailSenderService emailSender;
    private RegistrationTokenFactory tokenFactory;
    private EmailTemplateBuilder templateProvider;
    private ValidateEmailService service;

    @BeforeEach
    void setUp() {
        authUserRepository = mock(AuthUserRepository.class);
        tokenRepository = mock(RegistrationTokenRepository.class);
        emailSender = mock(EmailSenderService.class);
        tokenFactory = mock(RegistrationTokenFactory.class);
        templateProvider = mock(EmailTemplateBuilder.class);

        service = new ValidateEmailService(authUserRepository, tokenRepository, emailSender,
                tokenFactory, templateProvider);

        ReflectionTestUtils.setField(service, "registerUrl", "http://localhost:8080/register/");
    }

    @Test
    void shouldValidateAndSendLinkSuccessfully() {
        EmailModelRequest request = new EmailModelRequest("teste@exemplo.com");
        when(authUserRepository.existsByEmail("teste@exemplo.com")).thenReturn(false);

        RegistrationToken token = new RegistrationToken(null, "teste@exemplo.com", "token123",
                null, AuthUserRole.MENTOR);
        when(tokenFactory.create("teste@exemplo.com", AuthUserRole.MENTOR)).thenReturn(token);
        when(templateProvider.buildMentorRegisterEmail(anyString())).thenReturn("<html>link</html>");

        service.validateAndSendLink(request, AuthUserRole.MENTOR);

        verify(tokenRepository).save(token);
        verify(emailSender).sendEmail(eq("teste@exemplo.com"), eq("Complete seu cadastro"), anyString());
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {
        EmailModelRequest request = new EmailModelRequest("teste@exemplo.com");
        when(authUserRepository.existsByEmail("teste@exemplo.com")).thenReturn(true);

        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class,
                () -> service.validateAndSendLink(request, AuthUserRole.MENTOR));

        assertEquals("Email já cadastrado: teste@exemplo.com", ex.getMessage());
    }

    @Test
    void shouldRecoverAndSaveTokenIfSaveFails() {
        EmailModelRequest request = new EmailModelRequest("teste@exemplo.com");
        when(authUserRepository.existsByEmail("teste@exemplo.com")).thenReturn(false);

        RegistrationToken token = new RegistrationToken(null, "teste@exemplo.com", "token123",
                null, AuthUserRole.MENTOR);
        when(tokenFactory.create("teste@exemplo.com", AuthUserRole.MENTOR)).thenReturn(token);

        RegistrationToken oldToken = new RegistrationToken(null, "teste@exemplo.com", "oldToken",
                null, AuthUserRole.MENTOR);
        when(tokenRepository.findByEmail("teste@exemplo.com")).thenReturn(java.util.Optional.of(oldToken));

        when(tokenRepository.save(token))
                .thenThrow(new RuntimeException("DB error"))
                .thenReturn(token);

        when(templateProvider.buildMentorRegisterEmail(anyString())).thenReturn("<html>link</html>");

        service.validateAndSendLink(request, AuthUserRole.MENTOR);

        verify(tokenRepository).deleteByEmail("teste@exemplo.com");
        verify(tokenRepository, times(2)).save(token);
        verify(emailSender).sendEmail(eq("teste@exemplo.com"), eq("Complete seu cadastro"), anyString());
    }

    @Test
    void shouldThrowInternalErrorExceptionIfEmailSendFails() {
        EmailModelRequest request = new EmailModelRequest("teste@exemplo.com");
        when(authUserRepository.existsByEmail("teste@exemplo.com")).thenReturn(false);

        RegistrationToken token = new RegistrationToken(null, "teste@exemplo.com", "token123",
                null, AuthUserRole.MENTOR);
        when(tokenFactory.create("teste@exemplo.com", AuthUserRole.MENTOR)).thenReturn(token);

        when(templateProvider.buildMentorRegisterEmail(anyString())).thenReturn("<html>link</html>");
        doThrow(new RuntimeException("SMTP error")).when(emailSender)
                .sendEmail(anyString(), anyString(), anyString());

        InternalErrorException ex = assertThrows(InternalErrorException.class,
                () -> service.validateAndSendLink(request, AuthUserRole.MENTOR));

        assertEquals("Erro ao enviar e-mail de validação", ex.getMessage());
        verify(tokenRepository).delete(token);
    }
}
