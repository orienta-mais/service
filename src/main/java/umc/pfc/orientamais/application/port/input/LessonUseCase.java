package umc.pfc.orientamais.application.port.input;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.*;

public interface LessonUseCase {
  GenericModelResponse createLesson(CreateLessonModelRequest request);

  GenericModelResponse deleteLesson(@Valid String request);

  LessonDetailsModelResponse listLessonById(@Valid String request);

  GenericModelResponse updateLesson(String lessonId, @Valid UpdateLessonModelRequest request);

  List<LessonDetailsModelResponse> listLessonByMentorId(UUID request);

  List<LessonDetailsModelResponse> listLessonByMentoredId(UUID request);

  GenericModelResponse registerMentored(String lessonId);

  PagedModelResponse<LessonModelResponse> listLesson(
      String title, LocalDate date, String order, int page, int size);

  CountLessonsResponse countUpcomingAndUnavailabLessons();
}
