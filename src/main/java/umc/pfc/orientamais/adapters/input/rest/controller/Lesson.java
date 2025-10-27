package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.application.port.input.LessonUseCase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
public class Lesson {

    private final ModelMapper mapper;
    private final LessonUseCase lessonUseCase;

    @PostMapping
    public ResponseEntity<GenericModelResponse> createLesson(@Valid @RequestBody CreateLessonModelRequest request) {
        GenericModelResponse response = lessonUseCase.createLesson(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonModelResponse> listLessonById(@Valid @PathVariable String lessonId) {
        LessonModelResponse response = lessonUseCase.listLessonById(lessonId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<GenericModelResponse> updateLesson(@PathVariable String lessonId, @Valid @RequestBody UpdatelessonModelRequest request) {
        GenericModelResponse response = lessonUseCase.updateLesson(lessonId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<GenericModelResponse> deleteLesson(@Valid @PathVariable String lessonId) {
        GenericModelResponse response = lessonUseCase.deleteLesson(lessonId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(response);
    }

    @GetMapping("/mentor/{mentorId}/lessons")
    public ResponseEntity<List<LessonModelResponse>> listLessonById(@Valid @PathVariable UUID mentorId) {
        List<LessonModelResponse> response = lessonUseCase.listLessonByMentorId(mentorId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/{lessonId}/register-mentored")
    public ResponseEntity<GenericModelResponse> registerMentored(@PathVariable String lessonId) {
        GenericModelResponse response = lessonUseCase.registerMentored(lessonId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/list-all-lessons")
    public ResponseEntity<List<LessonModelResponse>> listLesson(@RequestParam(required = false) String title,
                                                                @RequestParam(required = false) LocalDate date,
                                                                @RequestParam(required = false) String order) {
        List<LessonModelResponse> response = lessonUseCase.listLesson(title, date, order);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
