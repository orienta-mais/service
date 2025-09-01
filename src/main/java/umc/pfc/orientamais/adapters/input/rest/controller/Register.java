package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.RegisterUserModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.ValidateEmailModelResponse;
import umc.pfc.orientamais.application.port.input.RegisterUserUseCase;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;
import umc.pfc.orientamais.domain.model.Email;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Register {

    private final ModelMapper mapper;
    private final ValidateEmailUseCase validateEmailUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/validate-email")
    public ResponseEntity<ValidateEmailModelResponse> validateEmail(@Valid @RequestBody EmailModelRequest emailModelRequest) {
        var email = mapper.map(emailModelRequest, Email.class);
        ValidateEmailModelResponse validateEmailResponse = validateEmailUseCase.validateEmail(email);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(validateEmailResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserModelResponse> register(@Valid @RequestBody UserRegisterModelRequest userRegisterModelRequest) {
        RegisterUserModelResponse registerResponse = registerUserUseCase.registerUser(userRegisterModelRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registerResponse);
    }
}
