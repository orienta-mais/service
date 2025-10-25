package umc.pfc.orientamais.adapters.input.rest.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ListLessonsRequest {
    private String title;
    private LocalDate date;
}
