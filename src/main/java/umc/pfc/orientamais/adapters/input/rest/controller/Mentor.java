package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorReviewRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.*;
import umc.pfc.orientamais.application.port.input.*;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class Mentor {

  private final ValidateEmailUseCase validateEmailUseCase;
  private final RegisterUseCase registerUseCase;
  private final MentorUseCase mentorUseCase;
  private final MentorReviewUseCase mentorReviewUseCase;

  @PostMapping("/validate-email")
  public ResponseEntity<GenericModelResponse> validateEmail(
      @Valid @RequestBody EmailModelRequest request) {
    validateEmailUseCase.validateAndSendLink(request, AuthUserRole.MENTOR);
    return ResponseEntity.ok(
        new GenericModelResponse("EMAIL_VALIDATED", "Validation link sent to email"));
  }

  @PostMapping("/register")
  public ResponseEntity<GenericModelResponse> register(
      @Valid @RequestBody UserRegisterModelRequest request) {
    registerUseCase.register(request, AuthUserRole.MENTOR);
    return ResponseEntity.ok(
        new GenericModelResponse("USER_CREATED", "User registered successfully"));
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
  public ResponseEntity<MentorModelResponse> updateMentor(
      @PathVariable UUID id, @Valid @RequestBody MentorUpdateModelRequest request) {
    return ResponseEntity.ok(mentorUseCase.updateMentor(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMentor(@PathVariable UUID id) {
    mentorUseCase.deleteMentor(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/mentor-review")
  public ResponseEntity<GenericModelResponse> addMentorReview(
      @PathVariable UUID id, @Valid @RequestBody MentorReviewRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mentorReviewUseCase.addMentorReview(id, request));
  }

  @GetMapping("/{id}/reviews")
  public ResponseEntity<List<MentorReviewResponse>> listReviews(@PathVariable UUID id) {
    return ResponseEntity.ok(mentorReviewUseCase.listMentorReviews(id));
  }
}
