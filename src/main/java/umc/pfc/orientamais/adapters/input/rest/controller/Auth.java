package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.LoginRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;
import umc.pfc.orientamais.application.port.input.LoginUseCase;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class Auth {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.login(request.email(), request.password()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(loginUseCase.refreshToken(refreshToken));
    }
}
