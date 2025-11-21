package umc.pfc.orientamais.application.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.output.zoom.CreateMeetingAdapter;
import umc.pfc.orientamais.domain.model.Lesson;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDate;
import java.util.ArrayList;
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
        var mentor = new Mentor();
        mentor.setId(request.getMentorId());
        lesson.setMentor(mentor);
        return lesson;
    }

    public Lesson requestToEntity(UpdatelessonModelRequest request, UUID id) {
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
        return lesson;
    }

    public List<LessonModelResponse> entityToResponse(List<Lesson> lessons) {
        var responses = new ArrayList<LessonModelResponse>();
        for (Lesson lesson : lessons) {
            var response = entityToResponse(lesson);
            responses.add(response);
        }
        return responses;
    }

    public LessonModelResponse entityToResponse(Lesson lesson) {
        var response = new LessonModelResponse();
        response.setId(lesson.getId());
        response.setTitle(lesson.getTitle());
        response.setDescription(lesson.getDescription());
        response.setLink(lesson.getLink());
        response.setMaxGuest(lesson.getMaxGuest());
        response.setPresentCode(lesson.getPresentCode());
        response.setDate(LocalDate.from(lesson.getEndTime()));
        var startTime = lesson.getStartTime().toLocalTime();
        var endTime = lesson.getEndTime().toLocalTime();
        response.setStartTime(startTime);
        response.setEndTime(endTime);
        response.setMentorName(lesson.getMentor().getName());
        response.setPresentCodeFilled(lesson.getPresentCodeFilled());
        return response;
    }
}
