package umc.pfc.orientamais.adapters.output.zoom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMeetingAdapterTest {

    @InjectMocks
    private CreateMeetingAdapter adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "accountId", "account");
        ReflectionTestUtils.setField(adapter, "clientId", "client");
        ReflectionTestUtils.setField(adapter, "clientSecret", "secret");
        ReflectionTestUtils.setField(adapter, "getAccessUrl", "https://zoom.com/token/");
        ReflectionTestUtils.setField(adapter, "createMeetingUrl", "https://zoom.com/meeting");
    }

    @Test
    void shouldReturnEmptyWhenTokenCallFails() throws Exception {
        HttpClient client = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> tokenResponse = mock(HttpResponse.class);

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class);
             MockedConstruction<ObjectMapper> ignored = mockConstruction(ObjectMapper.class)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(client);
            when(client.send(any(), any())).thenReturn((HttpResponse) tokenResponse);
            when(tokenResponse.statusCode()).thenReturn(500);

            String token = ReflectionTestUtils.invokeMethod(adapter, "returnZoomAccessToken");

            assertEquals("", token);
        }
    }

    @Test
    void shouldEncodeClientIdAndSecretEvenWhenNull() {
        ReflectionTestUtils.setField(adapter, "clientId", null);
        ReflectionTestUtils.setField(adapter, "clientSecret", null);

        String header = ReflectionTestUtils.invokeMethod(adapter, "encodeClientIdAndSecret");

        assertTrue(header.startsWith("Basic "));
        String encoded = header.replace("Basic ", "");
        String decoded = new String(java.util.Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        assertEquals(":", decoded);
    }
}
