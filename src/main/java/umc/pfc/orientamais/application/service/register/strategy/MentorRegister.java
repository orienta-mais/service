package umc.pfc.orientamais.application.service.register.strategy;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

@Service
@AllArgsConstructor
public class MentorRegister implements RoleRegistrationStrategy {

    private final ModelMapper mapper;
    private final MentorRepository mentorRepository;

    @Override
    public boolean supports(String role) {
        return "MENTOR".equalsIgnoreCase(role);
    }

    @Override
    public void register(UserRegisterModelRequest request, AuthUser user) {
        Mentor mentor = mapper.map(request, Mentor.class);
        mentor.setUser(user);
        mentorRepository.save(mentor);
    }
}
