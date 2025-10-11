package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ListlessonByMentorIdModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.application.port.input.RegisterMentorUseCase;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class Mentor {

    private final ValidateEmailUseCase validateEmailUseCase;
    private final RegisterMentorUseCase registerMentorUseCase;
    private final LessonUseCase lessonUseCase;

    @PostMapping("/validate-email")
    public ResponseEntity<GenericModelResponse> validateEmail(@Valid @RequestBody EmailModelRequest request) {
        validateEmailUseCase.validateAndSendLink(request);
        return ResponseEntity.ok(
                new GenericModelResponse("EMAIL_VALIDATED", "Validation link sent to email"));
    }

    @PostMapping("/register")
    public ResponseEntity<GenericModelResponse> register(@Valid @RequestBody UserRegisterModelRequest request) {
        registerMentorUseCase.register(request);
        return ResponseEntity.ok(
                new GenericModelResponse("USER_CREATED", "User registered successfully"));
    }

    @GetMapping("/leason/find-all")
    public ResponseEntity<?> listlessonById(@Valid @RequestParam ListlessonByMentorIdModelRequest request) {
        try {
            var reponse = lessonUseCase.listLeasonByMentorId(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error listing lesson: " + e.getMessage()));
        }
    }
}
