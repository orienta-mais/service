package umc.pfc.orientamais.application.port.input;

import jakarta.validation.Valid;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountLessonsResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.PagedModelResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LessonUseCase {
    GenericModelResponse createLesson(CreateLessonModelRequest request);

    GenericModelResponse deleteLesson(@Valid String request);

    LessonModelResponse listLessonById(@Valid String request);

    GenericModelResponse updateLesson(String lessonId, @Valid UpdateLessonModelRequest request);

    List<LessonModelResponse> listLessonByMentorId(UUID request);

    List<LessonModelResponse> listLessonByMentoredId(UUID request);

    GenericModelResponse registerMentored(String lessonId);

    PagedModelResponse<LessonModelResponse> listLesson(String title, LocalDate date, String order, int page, int size);

    CountLessonsResponse countUpcomingAndUnavailabLessons();
}
