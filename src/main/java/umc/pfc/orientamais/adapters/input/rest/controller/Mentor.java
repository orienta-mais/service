package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorModelResponse;
import umc.pfc.orientamais.application.port.input.*;
import umc.pfc.orientamais.domain.model.AuthUserRole;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class Mentor {

    private final ValidateEmailUseCase validateEmailUseCase;
    private final RegisterUseCase registerUseCase;
    private final MentorUseCase mentorUseCase;
    private final LessonUseCase lessonUseCase;
    private final MentorReviewUseCase mentorReviewUseCase;

    @PostMapping("/validate-email")
    public ResponseEntity<GenericModelResponse> validateEmail(@Valid @RequestBody EmailModelRequest request) {
        validateEmailUseCase.validateAndSendLink(request, AuthUserRole.MENTOR);
        return ResponseEntity.ok(new GenericModelResponse("EMAIL_VALIDATED", "Validation link sent to email"));
    }

    @PostMapping("/register")
    public ResponseEntity<GenericModelResponse> register(@Valid @RequestBody UserRegisterModelRequest request) {
        registerUseCase.register(request, AuthUserRole.MENTOR);
        return ResponseEntity.ok(new GenericModelResponse("USER_CREATED", "User registered successfully"));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MentorModelResponse>> getAllMentors() {
        return ResponseEntity.ok(mentorUseCase.getAllMentors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorModelResponse> getMentorById(@PathVariable UUID id) {
        return ResponseEntity.ok(mentorUseCase.getMentorById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MentorModelResponse> updateMentor(@PathVariable UUID id, @Valid @RequestBody MentorUpdateModelRequest request) {
        return ResponseEntity.ok(mentorUseCase.updateMentor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMentor(@PathVariable UUID id) {
        mentorUseCase.deleteMentor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse>> listReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(mentorReviewUseCase.listMentorReviews(id));
    }
}
