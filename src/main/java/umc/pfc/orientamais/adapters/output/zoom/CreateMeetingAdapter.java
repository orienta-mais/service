package umc.pfc.orientamais.adapters.output.zoom;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.application.port.output.zoom.CreateMeetingPort;

@Component
@RequiredArgsConstructor
public class CreateMeetingAdapter implements CreateMeetingPort {

  @Value("${zoom.account.id}")
  private String accountId;

  @Value("${zoom.client.id}")
  private String clientId;

  @Value("${zoom.client.secret}")
  private String clientSecret;

  @Value("${zoom.getAccess.url}")
  private String getAccessUrl;

  @Value("${zoom.createMeeting.url}")
  private String createMeetingUrl;

  @Override
  public String returnMeetingUrl() {
    try {
      String token = returnZoomAccessToken();
      var client = java.net.http.HttpClient.newHttpClient();
      var uri = java.net.URI.create(createMeetingUrl);
      var mapper = new com.fasterxml.jackson.databind.ObjectMapper();

      var payload = mapper.createObjectNode();
      payload.put("topic", "Reunião Created By OrientaMais " + LocalDate.now());
      payload.put("type", 2);
      payload.put("join_before_host", true);
      var settings = mapper.createObjectNode();
      settings.put("host_video", true);
      settings.put("participant_video", true);
      payload.set("settings", settings);
      String body = mapper.writeValueAsString(payload);

      var request =
          java.net.http.HttpRequest.newBuilder()
              .uri(uri)
              .header("authorization", "Bearer " + token)
              .header("content-type", "application/json")
              .POST(
                  java.net.http.HttpRequest.BodyPublishers.ofString(
                      body, java.nio.charset.StandardCharsets.UTF_8))
              .build();

      var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        var node = mapper.readTree(response.body());
        return node.get("join_url").asText();
      }
    } catch (Exception ignored) {

    }
    return "";
  }

  private String returnZoomAccessToken() {
    try {
      var client = java.net.http.HttpClient.newHttpClient();
      var uri = java.net.URI.create(getAccessUrl + accountId);
      var request =
          java.net.http.HttpRequest.newBuilder()
              .uri(uri)
              .header("authorization", encodeClientIdAndSecret())
              .header("content-type", "application/x-www-form-urlencoded")
              .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
              .build();
      var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        var node = mapper.readTree(response.body());
        if (node.has("access_token")) {
          return node.get("access_token").asText();
        }
      }
    } catch (Exception ignored) {
    }
    return "";
  }

  private String encodeClientIdAndSecret() {
    String id = clientId == null ? "" : clientId;
    String secret = clientSecret == null ? "" : clientSecret;
    String combined = id + ":" + secret;
    var encoded =
        java.util.Base64.getEncoder()
            .encodeToString(combined.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    return "Basic " + encoded;
  }
}
