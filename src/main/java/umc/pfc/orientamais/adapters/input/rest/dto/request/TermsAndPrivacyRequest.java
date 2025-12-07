package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import umc.pfc.orientamais.domain.model.terms.TermType;
import umc.pfc.orientamais.domain.validation.SafeInput;

public record TermsAndPrivacyRequest(
    @NotNull(message = "Tipo de termo é obrigatório") TermType type,
    @NotBlank(message = "Conteúdo é obrigatório")
        @Size(min = 10, max = 100000, message = "Conteúdo deve ter entre 10 e 100000 caracteres")
        @SafeInput(
            checkFor = {
              SafeInput.InjectionType.SQL_INJECTION,
              SafeInput.InjectionType.NULL_BYTES,
              SafeInput.InjectionType.PATH_TRAVERSAL,
              SafeInput.InjectionType.COMMAND_INJECTION
            },
            allowHtml = true,
            message = "Conteúdo contém caracteres ou padrões suspeitos de injeção")
        String content) {}
