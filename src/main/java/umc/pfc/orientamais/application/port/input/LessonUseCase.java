package umc.pfc.orientamais.application.port.input;

import jakarta.validation.Valid;
import umc.pfc.orientamais.adapters.input.rest.dto.request.*;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LessonUseCase {
    GenericModelResponse createlesson(CreatelessonModelRequest request);

    GenericModelResponse deletelesson(@Valid String request);

    LessonModelResponse listlessonById(@Valid String request);

    GenericModelResponse updatelesson(String lessonId, @Valid UpdatelessonModelRequest request);

    List<LessonModelResponse> listLeasonByMentorId(UUID request);

    List<LessonModelResponse> listLesson(String title, LocalDate date, String order);
}
