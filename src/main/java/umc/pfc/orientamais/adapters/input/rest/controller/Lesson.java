package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.DeletelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.application.port.input.LessonUseCase;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
public class Lesson {

    private final ModelMapper mapper;
    private final LessonUseCase lessonUseCase;

    @PostMapping("/create-lesson")
    public ResponseEntity<GenericModelResponse> createlesson(@Valid @RequestBody CreatelessonModelRequest request) {
        try {
            GenericModelResponse reponse = lessonUseCase.createlesson(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error creating lesson: " + e.getMessage()));
        }
    }

    @GetMapping("/list-lesson-by-id")
    public ResponseEntity<?> listlessonById(@Valid @RequestParam String lessonId) {
        try {
            var reponse = lessonUseCase.listlessonById(lessonId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error listing lesson: " + e.getMessage()));
        }
    }

    @PutMapping("/update-lesson")
    public ResponseEntity<GenericModelResponse> updatelesson(@Valid @RequestBody UpdatelessonModelRequest request) {
        try {
            GenericModelResponse reponse = lessonUseCase.updatelesson(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error updating lesson: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-lesson")
    public ResponseEntity<GenericModelResponse> deletelesson(@Valid @RequestBody DeletelessonModelRequest request) {
        try {
            GenericModelResponse reponse = lessonUseCase.deletelesson(request);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error deleting lesson: " + e.getMessage()));
        }
    }
}
