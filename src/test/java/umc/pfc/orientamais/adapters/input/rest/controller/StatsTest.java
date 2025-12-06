package umc.pfc.orientamais.adapters.input.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountByStateResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountLessonsResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountMentoredsResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountMentorsResponse;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.application.port.input.MentorUseCase;
import umc.pfc.orientamais.application.port.input.MentoredUseCase;

@ExtendWith(MockitoExtension.class)
class StatsTest {

  @InjectMocks private Stats statsController;

  @Mock private MentoredUseCase mentoredUseCase;

  @Mock private MentorUseCase mentorUseCase;

  @Mock private LessonUseCase lessonUseCase;

  private CountMentoredsResponse countMentoredsResponse;
  private CountByStateResponse countByStateResponse;
  private CountMentorsResponse countMentorsResponse;
  private CountLessonsResponse countLessonsResponse;

  @BeforeEach
  void setUp() {
    countMentoredsResponse = new CountMentoredsResponse();
    countMentoredsResponse.setMentoreds(10);

    countByStateResponse = new CountByStateResponse();
    countByStateResponse.setTotal(List.of());

    countMentorsResponse = new CountMentorsResponse();
    countMentorsResponse.setMentors(5);

    countLessonsResponse = new CountLessonsResponse();
    countLessonsResponse.setCountUpcomingLessons(2);
    countLessonsResponse.setCountUnavailableLessons(1);
  }

  @Test
  void shouldCountMentoredsSuccessfully() {
    when(mentoredUseCase.countMentoreds()).thenReturn(10);

    ResponseEntity<CountMentoredsResponse> response = statsController.countMentoreds();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(10, response.getBody().getMentoreds());
    verify(mentoredUseCase).countMentoreds();
  }

  @Test
  void shouldPropagateExceptionWhenCountMentoredsFails() {
    when(mentoredUseCase.countMentoreds()).thenThrow(new RuntimeException("count error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> statsController.countMentoreds());

    assertEquals("count error", exception.getMessage());
    verify(mentoredUseCase).countMentoreds();
  }

  @Test
  void shouldReturnZeroWhenMentoredCountIsNull() {
    when(mentoredUseCase.countMentoreds()).thenReturn(null);

    ResponseEntity<CountMentoredsResponse> response = statsController.countMentoreds();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNull(response.getBody().getMentoreds());
    verify(mentoredUseCase).countMentoreds();
  }

  @Test
  void shouldCountByStateSuccessfully() {
    when(mentoredUseCase.countMentoredsByState()).thenReturn(countByStateResponse);

    ResponseEntity<CountByStateResponse> response = statsController.countByState();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(countByStateResponse, response.getBody());
    verify(mentoredUseCase).countMentoredsByState();
  }

  @Test
  void shouldPropagateExceptionWhenCountByStateFails() {
    when(mentoredUseCase.countMentoredsByState()).thenThrow(new RuntimeException("state error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> statsController.countByState());

    assertEquals("state error", exception.getMessage());
    verify(mentoredUseCase).countMentoredsByState();
  }

  @Test
  void shouldCountMentorsSuccessfully() {
    when(mentorUseCase.countMentors()).thenReturn(5);

    ResponseEntity<CountMentorsResponse> response = statsController.countMentors();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(5, response.getBody().getMentors());
    verify(mentorUseCase).countMentors();
  }

  @Test
  void shouldPropagateExceptionWhenCountMentorsFails() {
    when(mentorUseCase.countMentors()).thenThrow(new RuntimeException("mentor error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> statsController.countMentors());

    assertEquals("mentor error", exception.getMessage());
    verify(mentorUseCase).countMentors();
  }

  @Test
  void shouldReturnNullMentorCountWhenUseCaseReturnsNull() {
    when(mentorUseCase.countMentors()).thenReturn(null);

    ResponseEntity<CountMentorsResponse> response = statsController.countMentors();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNull(response.getBody().getMentors());
    verify(mentorUseCase).countMentors();
  }

  @Test
  void shouldCountLessonsSuccessfully() {
    when(lessonUseCase.countUpcomingAndUnavailabLessons()).thenReturn(countLessonsResponse);

    ResponseEntity<CountLessonsResponse> response = statsController.countUpcomingLessons();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(countLessonsResponse, response.getBody());
    verify(lessonUseCase).countUpcomingAndUnavailabLessons();
  }

  @Test
  void shouldPropagateExceptionWhenCountLessonsFails() {
    when(lessonUseCase.countUpcomingAndUnavailabLessons())
        .thenThrow(new RuntimeException("lesson error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> statsController.countUpcomingLessons());

    assertEquals("lesson error", exception.getMessage());
    verify(lessonUseCase).countUpcomingAndUnavailabLessons();
  }

  @Test
  void shouldHandleNullLessonsResponse() {
    when(lessonUseCase.countUpcomingAndUnavailabLessons()).thenReturn(null);

    ResponseEntity<CountLessonsResponse> response = statsController.countUpcomingLessons();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNull(response.getBody());
    verify(lessonUseCase).countUpcomingAndUnavailabLessons();
  }
}
