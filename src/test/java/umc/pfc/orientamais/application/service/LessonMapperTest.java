package umc.pfc.orientamais.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.output.zoom.CreateMeetingAdapter;
import umc.pfc.orientamais.application.mapper.LessonMapper;
import umc.pfc.orientamais.domain.model.Lesson;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LessonMapperTest {

    @InjectMocks
    private LessonMapper lessonMapper;
    @Mock
    private CreateMeetingAdapter createMeetingAdapter;

    private CreateLessonModelRequest createRequest;
    private UpdatelessonModelRequest updateRequest;
    private Lesson lessonEntity;

    @BeforeEach
    void setUp() {
        Mentor mentor = new Mentor();
        mentor.setId(UUID.randomUUID());
        mentor.setName("John Mentor");

        createRequest = new CreateLessonModelRequest();
        createRequest.setTitle("Intro to Java");
        createRequest.setDescription("Basic syntax and core concepts");
        createRequest.setMaxGuest(10);
        createRequest.setDate(LocalDate.of(2025, 10, 20));
        createRequest.setStartTime(LocalTime.of(14, 0));
        createRequest.setEndTime(LocalTime.of(16, 0));
        createRequest.setMentorId(mentor.getId());
        createRequest.setPresentCode("JAVA101");

        updateRequest = new UpdatelessonModelRequest();
        updateRequest.setTitle("Advanced Java");
        updateRequest.setDescription("Streams and Lambdas");
        updateRequest.setMaxGuest(15);
        updateRequest.setDate(LocalDate.of(2025, 10, 22));
        updateRequest.setStartTime(LocalTime.of(15, 0));
        updateRequest.setEndTime(LocalTime.of(17, 0));
        updateRequest.setMentorId(mentor.getId());
        updateRequest.setPresentCode("JAVA202");

        lessonEntity = new Lesson();
        lessonEntity.setId(UUID.randomUUID());
        lessonEntity.setTitle("Clean Code");
        lessonEntity.setDescription("Writing maintainable Java code");
        lessonEntity.setLink("https://example.com/clean-code");
        lessonEntity.setMaxGuest(8);
        lessonEntity.setStartTime(LocalDateTime.of(2025, 10, 23, 13, 0));
        lessonEntity.setEndTime(LocalDateTime.of(2025, 10, 23, 15, 0));
        lessonEntity.setMentor(mentor);
        lessonEntity.setPresentCode("CLEAN123");
    }

    @Test
    void shouldMapCreateRequestToEntitySuccessfully() {
        Lesson lesson = lessonMapper.requestToEntity(createRequest);

        assertEquals(createRequest.getTitle(), lesson.getTitle());
        assertEquals(createRequest.getDescription(), lesson.getDescription());
        assertEquals(createRequest.getMaxGuest(), lesson.getMaxGuest());
        assertEquals(createRequest.getPresentCode(), lesson.getPresentCode());
        assertEquals(createRequest.getMentorId(), lesson.getMentor().getId());
        assertEquals(createRequest.getDate().atTime(createRequest.getStartTime()), lesson.getStartTime());
        assertEquals(createRequest.getDate().atTime(createRequest.getEndTime()), lesson.getEndTime());
    }

    @Test
    void shouldMapUpdateRequestToEntitySuccessfully() {
        UUID lessonId = UUID.randomUUID();

        Lesson lesson = lessonMapper.requestToEntity(updateRequest, lessonId);

        assertEquals(lessonId, lesson.getId());
        assertEquals(updateRequest.getTitle(), lesson.getTitle());
        assertEquals(updateRequest.getDescription(), lesson.getDescription());
        assertEquals(updateRequest.getMaxGuest(), lesson.getMaxGuest());
        assertEquals(updateRequest.getPresentCode(), lesson.getPresentCode());
        assertEquals(updateRequest.getMentorId(), lesson.getMentor().getId());
        assertEquals(updateRequest.getDate().atTime(updateRequest.getStartTime()), lesson.getStartTime());
        assertEquals(updateRequest.getDate().atTime(updateRequest.getEndTime()), lesson.getEndTime());
    }

    @Test
    void shouldMapEntityToResponseSuccessfully() {
        LessonModelResponse response = lessonMapper.entityToResponse(lessonEntity);

        assertEquals(lessonEntity.getId(), response.getId());
        assertEquals(lessonEntity.getTitle(), response.getTitle());
        assertEquals(lessonEntity.getDescription(), response.getDescription());
        assertEquals(lessonEntity.getLink(), response.getLink());
        assertEquals(lessonEntity.getMaxGuest(), response.getMaxGuest());
        assertEquals(lessonEntity.getStartTime().toLocalTime(), response.getStartTime());
        assertEquals(lessonEntity.getEndTime().toLocalTime(), response.getEndTime());
        assertEquals(lessonEntity.getMentor().getName(), response.getMentorName());
        assertEquals(LocalDate.from(lessonEntity.getEndTime()), response.getDate());
    }

    @Test
    void shouldMapEntityListToResponseListSuccessfully() {
        List<Lesson> lessons = List.of(lessonEntity);
        List<LessonModelResponse> responses = lessonMapper.entityToResponse(lessons);

        assertEquals(1, responses.size());
        LessonModelResponse response = responses.getFirst();
        assertEquals(lessonEntity.getTitle(), response.getTitle());
        assertEquals(lessonEntity.getMentor().getName(), response.getMentorName());
    }
}

