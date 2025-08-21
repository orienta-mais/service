package umc.pfc.orientamais.adapters.input.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailModelRequest {
    @NotBlank(message = "Campo e-mail necessário.")
    @Email(message = "Campo e-mail incorreto")
    @JsonProperty("email")
    private String email;
}
