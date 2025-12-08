package umc.pfc.orientamais.application.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonDetailsModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.output.zoom.CreateMeetingAdapter;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LessonMapper {

  @Autowired
  private final CreateMeetingAdapter createMeetingAdapter;

  public Lesson requestToEntity(CreateLessonModelRequest request) {
    var lesson = new Lesson();
    lesson.setTitle(request.getTitle());
    lesson.setDescription(request.getDescription());
    lesson.setLink(createMeetingAdapter.returnMeetingUrl());
    lesson.setMaxGuest(request.getMaxGuest());
    lesson.setStartTime(request.getDate().atTime(request.getStartTime()));
    lesson.setEndTime(request.getDate().atTime(request.getEndTime()));
    lesson.setPresentCode(request.getPresentCode());
    lesson.setAdditionalLinks(
      request.getAdditionalLinks() == null ? new ArrayList<>() : request.getAdditionalLinks());
    var mentor = new Mentor();
    mentor.setId(request.getMentorId());
    lesson.setMentor(mentor);
    return lesson;
  }

  public Lesson requestToEntity(UpdateLessonModelRequest request, UUID id) {
    var lesson = new Lesson();
    lesson.setId(id);
    lesson.setTitle(request.getTitle());
    lesson.setDescription(request.getDescription());
    lesson.setLink(createMeetingAdapter.returnMeetingUrl());
    lesson.setMaxGuest(request.getMaxGuest());
    lesson.setStartTime(request.getDate().atTime(request.getStartTime()));
    lesson.setEndTime(request.getDate().atTime(request.getEndTime()));
    lesson.setPresentCode(request.getPresentCode());
    var mentor = new Mentor();
    mentor.setId(request.getMentorId());
    lesson.setMentor(mentor);
    lesson.setAdditionalLinks(
      request.getAdditionalLinks() == null
        ? Collections.emptyList()
        : request.getAdditionalLinks());
    return lesson;
  }

  public List<LessonDetailsModelResponse> entityToDetailsResponse(List<Lesson> lessons) {
    var responses = new ArrayList<LessonDetailsModelResponse>();
    for (Lesson lesson : lessons) {
      var response = entityToDetailsResponse(lesson);
      responses.add(response);
    }
    return responses;
  }

  public LessonDetailsModelResponse entityToDetailsResponse(Lesson lesson) {
    var response = new LessonDetailsModelResponse();

    response.setId(lesson.getId());
    response.setTitle(lesson.getTitle());
    response.setDescription(lesson.getDescription());
    response.setLink(lesson.getLink());
    response.setMaxGuest(lesson.getMaxGuest());
    response.setPresentCode(lesson.getPresentCode());

    response.setDate(lesson.getStartTime().toLocalDate());
    response.setStartTime(lesson.getStartTime().toLocalTime());
    response.setEndTime(lesson.getEndTime().toLocalTime());

    try {
      if (lesson.getMentor() != null) {
        response.setMentorId(lesson.getMentor().getId());
        response.setMentorName(lesson.getMentor().getName());
      } else {
        response.setMentorId(null);
        response.setMentorName(null);
      }
    } catch (jakarta.persistence.EntityNotFoundException e) {
      response.setMentorId(null);
      response.setMentorName(null);
    }

    response.setAdditionalLinks(
      lesson.getAdditionalLinks() == null
        ? Collections.emptyList()
        : lesson.getAdditionalLinks()
    );

    response.setPresentCodeFilled(null);

    return response;
  }


  public LessonModelResponse entityToResponse(Lesson lesson) {
    var response = new LessonModelResponse();
    response.setId(lesson.getId());
    response.setTitle(lesson.getTitle());
    response.setDescription(lesson.getDescription());
    response.setMaxGuest(lesson.getMaxGuest());
    response.setDate(LocalDate.from(lesson.getEndTime()));
    var startTime = lesson.getStartTime().toLocalTime();
    var endTime = lesson.getEndTime().toLocalTime();
    response.setStartTime(startTime);
    response.setEndTime(endTime);

    try {
      if (lesson.getMentor() != null) {
        response.setMentorName(lesson.getMentor().getName());
      } else {
        response.setMentorName(null);
      }
    } catch (jakarta.persistence.EntityNotFoundException e) {
      response.setMentorName(null);
    }

    return response;
  }
}
