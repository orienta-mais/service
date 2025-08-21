package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.RegisterUserModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.application.port.input.RegisterUserUseCase;
import umc.pfc.orientamais.application.service.register.strategy.RoleRegistrationStrategy;
import umc.pfc.orientamais.domain.model.AuthUser;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final AuthUserRepository authUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final List<RoleRegistrationStrategy> strategies;
    private final ModelMapper mapper;

    @Override
    public RegisterUserModelResponse registerUser(UserRegisterModelRequest request) {
        AuthUser user = mapper.map(request, AuthUser.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        authUserRepository.save(user);

        strategies.stream()
                .filter(s -> s.supports(request.getRole()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Role inválida!: " + request.getRole()))
                .register(request, user);

        return new RegisterUserModelResponse("USER_CREATED", "Usuário criado com sucesso!");
    }
}
