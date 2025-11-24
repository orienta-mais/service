package umc.pfc.orientamais.adapters.input.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountByStateResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountLessonsResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountMentoredsResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountMentorsResponse;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.application.port.input.MentorUseCase;
import umc.pfc.orientamais.application.port.input.MentoredUseCase;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class Stats {

    private final MentoredUseCase mentoredUseCase;
    private final MentorUseCase mentorUseCase;
    private final LessonUseCase lessonUseCase;

    @GetMapping("/count-mentoreds")
    public ResponseEntity<CountMentoredsResponse> countMentoreds() {
        var mentorsResponse = new CountMentoredsResponse();
        mentorsResponse.setMentoreds(mentoredUseCase.countMentoreds());
        return ResponseEntity.status(HttpStatus.OK).body(mentorsResponse);
    }

    @GetMapping("/count-by-state")
    public ResponseEntity<CountByStateResponse> countByState() {
        var response = mentoredUseCase.countMentoredsByState();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/count-mentors")
    public ResponseEntity<CountMentorsResponse> countMentors() {
        var mentorsResponse = new CountMentorsResponse();
        mentorsResponse.setMentors(mentorUseCase.countMentors());
        return ResponseEntity.status(HttpStatus.OK).body(mentorsResponse);
    }

    @GetMapping("/count-lessons")
    public ResponseEntity<CountLessonsResponse> countUpcomingLessons() {
        CountLessonsResponse response = lessonUseCase.countUpcomingAndUnavailabLessons();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
