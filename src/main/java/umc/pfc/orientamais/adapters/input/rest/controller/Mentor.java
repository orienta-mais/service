package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.application.port.input.RegisterMentorUseCase;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class Mentor {

    private final ValidateEmailUseCase validateEmailUseCase;
    private final RegisterMentorUseCase registerMentorUseCase;

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
}
