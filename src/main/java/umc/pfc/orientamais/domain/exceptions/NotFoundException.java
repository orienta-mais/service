package umc.pfc.orientamais.domain.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
