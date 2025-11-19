package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.PresenceCodeModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.*;
import umc.pfc.orientamais.application.port.input.CertificateUseCase;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.application.port.input.MentorUseCase;
import umc.pfc.orientamais.application.port.input.MentoredUseCase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        var response =mentoredUseCase.countMentoredsByState();
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
