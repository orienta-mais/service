package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ListlessonByMentorIdModelRequest {
    @NotNull(message = "O ID do mentor é obrigatório.")
    private String mentorId;
}
