package umc.pfc.orientamais.application.service.register.strategy;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@Service
@AllArgsConstructor
public class MentoredRegister implements RoleRegistrationStrategy {

    private final MentoredRepository mentoredRepository;
    private final ModelMapper mapper;

    @Override
    public boolean supports(String role) {
        return "MENTORED".equalsIgnoreCase(role);
    }

    @Override
    public void register(UserRegisterModelRequest request, AuthUser user) {
        Mentored mentored = mapper.map(request, Mentored.class);
        mentored.setUser(user);
        mentoredRepository.save(mentored);
    }
}
