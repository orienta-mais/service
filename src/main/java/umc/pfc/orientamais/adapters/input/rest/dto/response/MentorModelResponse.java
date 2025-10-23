package umc.pfc.orientamais.adapters.input.rest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class MentorModelResponse {
    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String socialMedias;
    private String description;
    private String state;
    private String nationality;
}