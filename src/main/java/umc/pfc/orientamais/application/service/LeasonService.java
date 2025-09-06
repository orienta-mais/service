package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.controller.DeleteLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.controller.ListLeasonByIdModelRequest;
import umc.pfc.orientamais.adapters.input.rest.controller.UpdateLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LeasonModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.LeasonRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.application.port.input.LeasonUseCase;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.exceptions.UnsupportedRoleException;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.Leason;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class LeasonService implements LeasonUseCase {

    private final LeasonRepository leasonRepository;
    private final MentorRepository mentorRepository;
    private final ModelMapper mapper;

    @Override
    public GenericModelResponse createLeason(CreateLeasonModelRequest request) {
        var leason = mapper.map(request, Leason.class);
        mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        leasonRepository.save(leason);

        return new GenericModelResponse("LEASON_CREATED", "Leason created successfully!");
    }

    @Override
    public GenericModelResponse deleteLeason(DeleteLeasonModelRequest request) {
        UUID leasonId = UUID.fromString(request.getLeasonId());
        leasonRepository.findById(leasonId)
                .orElseThrow(() -> new NotFoundException("Leason não encontrado"));
        leasonRepository.deleteById(leasonId);

        return new GenericModelResponse("LEASON_DELETED", "Leason deleted successfully!");
    }

    @Override
    public LeasonModelResponse listLeasonById(ListLeasonByIdModelRequest request) {
        UUID leasonId = UUID.fromString(request.getLeasonId());
        var leason = leasonRepository.findById(leasonId)
                .orElseThrow(() -> new NotFoundException("Leason não encontrado"));
        return mapper.map(leason, LeasonModelResponse.class);
    }

    @Override
    public GenericModelResponse updateLeason(UpdateLeasonModelRequest request) {
        UUID leasonId = UUID.fromString(request.getId());
        var leason = mapper.map(request, Leason.class);
        leasonRepository.findById(leasonId)
                .orElseThrow(() -> new NotFoundException("Leason não encontrado"));
        mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        leasonRepository.save(leason);

        return new GenericModelResponse("LEASON_UPDATED", "Leason updated successfully!");
    }
}
