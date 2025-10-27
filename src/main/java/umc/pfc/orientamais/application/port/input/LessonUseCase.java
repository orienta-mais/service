package umc.pfc.orientamais.application.port.input;

import jakarta.validation.Valid;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LessonUseCase {
    GenericModelResponse createLesson(CreateLessonModelRequest request);

    GenericModelResponse deleteLesson(@Valid String request);

    LessonModelResponse listLessonById(@Valid String request);

    GenericModelResponse updateLesson(String lessonId, @Valid UpdatelessonModelRequest request);

    List<LessonModelResponse> listLessonByMentorId(UUID request);

    GenericModelResponse registerMentored(String lessonId);

    List<LessonModelResponse> listLesson(String title, LocalDate date, String order);
}
