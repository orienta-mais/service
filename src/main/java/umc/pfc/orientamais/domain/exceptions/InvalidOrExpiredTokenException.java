package umc.pfc.orientamais.domain.exceptions;

public class InvalidOrExpiredTokenException extends RuntimeException {
  public InvalidOrExpiredTokenException() {
    super("O token fornecido é inválido ou expirou!");
  }
}
