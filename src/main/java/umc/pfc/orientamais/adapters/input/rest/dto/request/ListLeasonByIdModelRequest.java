package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ListLeasonByIdModelRequest {
    @NotNull(message = "O ID da aula é obrigatório.")
    private String leasonId;
}
