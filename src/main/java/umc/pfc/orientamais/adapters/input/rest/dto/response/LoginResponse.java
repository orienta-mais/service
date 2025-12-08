package umc.pfc.orientamais.adapters.input.rest.dto.response;

public record LoginResponse(
    String status,
    String message,
    String accessToken,
    String refreshToken,
    Boolean termsAccepted) {}
