package umc.pfc.orientamais.domain.model.terms;

import lombok.Getter;

@Getter
public enum TermType {
  TERMS("Termos de Uso"),
  PRIVACY("Política de Privacidade");

  private final String displayName;

  TermType(String displayName) {
    this.displayName = displayName;
  }

}
