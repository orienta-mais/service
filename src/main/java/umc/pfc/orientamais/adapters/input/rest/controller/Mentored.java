package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentoredUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.*;
import umc.pfc.orientamais.application.port.input.MentoredUseCase;
import umc.pfc.orientamais.application.port.input.RegisterUseCase;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mentored")
@RequiredArgsConstructor
public class Mentored {

    private final ValidateEmailUseCase validateEmailUseCase;
    private final RegisterUseCase registerUseCase;
    private final MentoredUseCase mentoredUseCase;

    @PostMapping("/validate-email")
    public ResponseEntity<GenericModelResponse> validateEmail(@Valid @RequestBody EmailModelRequest request) {
        validateEmailUseCase.validateAndSendLink(request, AuthUserRole.MENTORED);
        return ResponseEntity.ok(new GenericModelResponse("EMAIL_VALIDATED", "Validation link sent to email"));
    }

    @PostMapping("/register")
    public ResponseEntity<GenericModelResponse> register(@Valid @RequestBody UserRegisterModelRequest request) {
        registerUseCase.register(request, AuthUserRole.MENTORED);
        return ResponseEntity.ok(new GenericModelResponse("USER_CREATED", "User registered successfully"));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MentoredModelResponse>> getAllMentoreds() {
        return ResponseEntity.ok(mentoredUseCase.getAllMentoreds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentoredModelResponse> getMentoredById(@PathVariable UUID id) {
        return ResponseEntity.ok(mentoredUseCase.getMentoredById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MentoredModelResponse> updateMentored(
            @PathVariable UUID id,
            @Valid @RequestBody MentoredUpdateModelRequest request) {
        return ResponseEntity.ok(mentoredUseCase.updateMentored(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMentored(@PathVariable UUID id) {
        mentoredUseCase.deleteMentored(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count-mentoreds")
    public ResponseEntity<CountMentoredsResponse> countMentors() {
        var mentorsResponse = new CountMentoredsResponse();
        mentorsResponse.setMentoreds(mentoredUseCase.countMentoreds());
        return ResponseEntity.status(HttpStatus.OK).body(mentorsResponse);
    }

    @GetMapping("/stats/count-by-state")
    public ResponseEntity<CountByStateResponse> countByState() {
        var response =mentoredUseCase.countMentoredsByState();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}