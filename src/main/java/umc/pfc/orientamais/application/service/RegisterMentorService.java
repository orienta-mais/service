package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.application.port.input.RegisterMentorUseCase;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.RegistrationToken;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterMentorService implements RegisterMentorUseCase {

    private final AuthUserRepository authUserRepository;
    private final RegistrationTokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MentorRepository mentorRepository;

    @Override
    @Transactional
    public void register(UserRegisterModelRequest request) {
        RegistrationToken token = tokenRepository.findByToken(request.token())
                .orElseThrow(InvalidOrExpiredTokenException::new);

        if (token.getExpiration().isBefore(LocalDateTime.now())) {
            throw new InvalidOrExpiredTokenException();
        }

        String encryptedPassword = passwordEncoder.encode(request.password());

        AuthUser user = new AuthUser(
                request.email(),
                encryptedPassword,
                request.role()
        );
        authUserRepository.save(user);

        Mentor mentor = new Mentor();
        mentor.setUser(user);
        mentor.setName(request.name());
        mentor.setLastName(request.lastName());
        mentor.setBirthDate(request.birthDate());
        mentor.setSocialMedias(request.socialMedias());
        mentor.setDescription(request.description());
        mentor.setState(request.state());
        mentor.setNationality(request.nationality());

        mentorRepository.save(mentor);

        tokenRepository.delete(token);
    }
}