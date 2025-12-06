package umc.pfc.orientamais.adapters.input.rest.dto.response;

import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.mentor.MentorInterest;

import java.time.LocalDate;
import java.util.List;
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
    private Integer totalClasses;
}
