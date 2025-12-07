package umc.pfc.orientamais.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonDetailsModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.output.zoom.CreateMeetingAdapter;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMapperTest {

    @InjectMocks
    private LessonMapper lessonMapper;

    @Mock
    private CreateMeetingAdapter createMeetingAdapter;

    private CreateLessonModelRequest createRequest;
    private UpdateLessonModelRequest updateRequest;
    private Lesson baseLesson;
    private Mentor mentor;

    @BeforeEach
    void setUp() {
        mentor = new Mentor();
        mentor.setId(UUID.randomUUID());
        mentor.setName("Mentor Name");

        createRequest = new CreateLessonModelRequest();
        createRequest.setTitle("Create Title");
        createRequest.setDescription("Create Description");
        createRequest.setMaxGuest(20);
        createRequest.setDate(LocalDate.of(2025, 1, 10));
        createRequest.setStartTime(LocalTime.of(10, 30));
        createRequest.setEndTime(LocalTime.of(11, 45));
        createRequest.setMentorId(mentor.getId());
        createRequest.setPresentCode("CODE-123");

        updateRequest = new UpdateLessonModelRequest();
        updateRequest.setTitle("Update Title");
        updateRequest.setDescription("Update Description");
        updateRequest.setMaxGuest(30);
        updateRequest.setDate(LocalDate.of(2025, 2, 11));
        updateRequest.setStartTime(LocalTime.of(15, 0));
        updateRequest.setEndTime(LocalTime.of(16, 30));
        updateRequest.setMentorId(mentor.getId());
        updateRequest.setPresentCode("CODE-456");

        baseLesson = new Lesson();
        baseLesson.setId(UUID.randomUUID());
        baseLesson.setTitle("Lesson");
        baseLesson.setDescription("Description");
        baseLesson.setLink("http://link");
        baseLesson.setMaxGuest(10);
        baseLesson.setStartTime(LocalDateTime.of(2025, 3, 1, 8, 0));
        baseLesson.setEndTime(LocalDateTime.of(2025, 3, 1, 9, 0));
        baseLesson.setMentor(mentor);
        baseLesson.setPresentCode("PCODE");
    }

    @Test
    void requestToEntityWithCreateRequestPopulatesFieldsAndGeneratesLink() {
        createRequest.setAdditionalLinks(List.of("a", "b"));
        when(createMeetingAdapter.returnMeetingUrl()).thenReturn("http://zoom.link");

        Lesson result = lessonMapper.requestToEntity(createRequest);

        assertEquals("Create Title", result.getTitle());
        assertEquals("Create Description", result.getDescription());
        assertEquals(20, result.getMaxGuest());
        assertEquals("CODE-123", result.getPresentCode());
        assertEquals(mentor.getId(), result.getMentor().getId());
        assertEquals(createRequest.getDate().atTime(createRequest.getStartTime()), result.getStartTime());
        assertEquals(createRequest.getDate().atTime(createRequest.getEndTime()), result.getEndTime());
        assertEquals(List.of("a", "b"), result.getAdditionalLinks());
        assertEquals("http://zoom.link", result.getLink());
        verify(createMeetingAdapter).returnMeetingUrl();
    }

    @Test
    void requestToEntityWithCreateRequestNullAdditionalLinksCreatesEmptyList() {
        createRequest.setAdditionalLinks(null);
        when(createMeetingAdapter.returnMeetingUrl()).thenReturn("http://zoom.link");

        Lesson result = lessonMapper.requestToEntity(createRequest);

        assertNotNull(result.getAdditionalLinks());
        assertTrue(result.getAdditionalLinks().isEmpty());
        verify(createMeetingAdapter).returnMeetingUrl();
    }

    @Test
    void requestToEntityWithUpdateRequestPopulatesFieldsAndId() {
        updateRequest.setAdditionalLinks(List.of("extra"));
        UUID lessonId = UUID.randomUUID();
        when(createMeetingAdapter.returnMeetingUrl()).thenReturn("http://zoom.link");

        Lesson result = lessonMapper.requestToEntity(updateRequest, lessonId);

        assertEquals(lessonId, result.getId());
        assertEquals("Update Title", result.getTitle());
        assertEquals("Update Description", result.getDescription());
        assertEquals(30, result.getMaxGuest());
        assertEquals("CODE-456", result.getPresentCode());
        assertEquals(mentor.getId(), result.getMentor().getId());
        assertEquals(updateRequest.getDate().atTime(updateRequest.getStartTime()), result.getStartTime());
        assertEquals(updateRequest.getDate().atTime(updateRequest.getEndTime()), result.getEndTime());
        assertEquals(List.of("extra"), result.getAdditionalLinks());
        verify(createMeetingAdapter).returnMeetingUrl();
    }

    @Test
    void requestToEntityWithUpdateRequestNullAdditionalLinksUsesEmptyList() {
        updateRequest.setAdditionalLinks(null);
        UUID lessonId = UUID.randomUUID();
        when(createMeetingAdapter.returnMeetingUrl()).thenReturn("http://zoom.link");

        Lesson result = lessonMapper.requestToEntity(updateRequest, lessonId);

        assertNotNull(result.getAdditionalLinks());
        assertTrue(result.getAdditionalLinks().isEmpty());
        verify(createMeetingAdapter).returnMeetingUrl();
    }

    @Test
    void entityToDetailsResponseMapsAllFields() {
        baseLesson.setAdditionalLinks(List.of("link1"));

        LessonDetailsModelResponse response = lessonMapper.entityToDetailsResponse(baseLesson);

        assertEquals(baseLesson.getId(), response.getId());
        assertEquals(baseLesson.getTitle(), response.getTitle());
        assertEquals(baseLesson.getDescription(), response.getDescription());
        assertEquals(baseLesson.getLink(), response.getLink());
        assertEquals(baseLesson.getMaxGuest(), response.getMaxGuest());
        assertEquals(baseLesson.getPresentCode(), response.getPresentCode());
        assertEquals(LocalDate.from(baseLesson.getEndTime()), response.getDate());
        assertEquals(baseLesson.getStartTime().toLocalTime(), response.getStartTime());
        assertEquals(baseLesson.getEndTime().toLocalTime(), response.getEndTime());
        assertEquals(baseLesson.getMentor().getId(), response.getMentorId());
        assertEquals(baseLesson.getMentor().getName(), response.getMentorName());
        assertEquals(List.of("link1"), response.getAdditionalLinks());
        assertNull(response.getPresentCodeFilled());
    }

    @Test
    void entityToDetailsResponseListHandlesMultipleLessons() {
        Lesson anotherLesson = new Lesson();
        anotherLesson.setId(UUID.randomUUID());
        anotherLesson.setMentor(mentor);
        anotherLesson.setTitle("Other");
        anotherLesson.setDescription("Other desc");
        anotherLesson.setLink("http://other");
        anotherLesson.setMaxGuest(5);
        anotherLesson.setPresentCode("other-code");
        anotherLesson.setStartTime(LocalDateTime.of(2025, 4, 1, 12, 0));
        anotherLesson.setEndTime(LocalDateTime.of(2025, 4, 1, 13, 0));

        List<LessonDetailsModelResponse> responses = lessonMapper.entityToDetailsResponse(List.of(baseLesson, anotherLesson));

        assertEquals(2, responses.size());
        assertEquals("Lesson", responses.get(0).getTitle());
        assertEquals("Other", responses.get(1).getTitle());
    }

    @Test
    void entityToResponseMapsSummaryFields() {
        LessonModelResponse response = lessonMapper.entityToResponse(baseLesson);

        assertEquals(baseLesson.getId(), response.getId());
        assertEquals(baseLesson.getTitle(), response.getTitle());
        assertEquals(baseLesson.getDescription(), response.getDescription());
        assertEquals(baseLesson.getMaxGuest(), response.getMaxGuest());
        assertEquals(LocalDate.from(baseLesson.getEndTime()), response.getDate());
        assertEquals(baseLesson.getStartTime().toLocalTime(), response.getStartTime());
        assertEquals(baseLesson.getEndTime().toLocalTime(), response.getEndTime());
        assertEquals(baseLesson.getMentor().getName(), response.getMentorName());
    }

    @Test
    void entityToDetailsResponseHandlesNullAdditionalLinks() {
        baseLesson.setAdditionalLinks(null);

        LessonDetailsModelResponse response = lessonMapper.entityToDetailsResponse(baseLesson);

        assertNotNull(response.getAdditionalLinks());
        assertTrue(response.getAdditionalLinks().isEmpty());
    }

    @Test
    void entityToDetailsResponseShouldThrowWhenMentorIsNull() {
        baseLesson.setMentor(null);

        assertThrows(NullPointerException.class, () -> lessonMapper.entityToDetailsResponse(baseLesson));
    }

    @Test
    void entityToResponseShouldThrowWhenMentorIsNull() {
        baseLesson.setMentor(null);

        assertThrows(NullPointerException.class, () -> lessonMapper.entityToResponse(baseLesson));
    }
}
