package umc.pfc.orientamais.adapters.input.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericModelResponse {

    private String code;
    private String message;
    private Object data;
    private Instant timestamp;

    public GenericModelResponse(String success, String message) {
        this.code = success;
        this.message = message;
    }

    public static GenericModelResponse success(String message, Object data) {
        return GenericModelResponse.builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static GenericModelResponse error(String code, String message) {
        return GenericModelResponse.builder()
                .code(code)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static GenericModelResponse message(String message) {
        return GenericModelResponse.builder()
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}
