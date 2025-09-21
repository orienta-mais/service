package umc.pfc.orientamais.domain.exceptions;

public class InvalidOrExpiredTokenException extends RuntimeException {
    public InvalidOrExpiredTokenException() {
        super("The provided registration token is invalid or expired.");
    }
}
