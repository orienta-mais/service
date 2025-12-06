package umc.pfc.orientamais.application.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorInfoModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

@ExtendWith(MockitoExtension.class)
class MentorMapperTest {

  @InjectMocks private MentorMapper mentorMapper;

  private Mentor mentor;
  private MentorUpdateModelRequest updateRequest;

  @BeforeEach
  void setUp() {
    AuthUser user = new AuthUser();
    user.setEmail("mentor@example.com");

    mentor = new Mentor();
    mentor.setId(UUID.randomUUID());
    mentor.setUser(user);
    mentor.setName("Grace");
    mentor.setLastName("Hopper");
    mentor.setBirthDate(LocalDate.of(1906, 12, 9));
    mentor.setSocialMedias("@grace");
    mentor.setDescription("COBOL inventor");
    mentor.setState("NY");
    mentor.setNationality("US");
    mentor.setClasses(new ArrayList<>());

    updateRequest =
        new MentorUpdateModelRequest(
            "Ada", "Lovelace", LocalDate.of(1815, 12, 10), "@ada", "Analytical Engine", "RJ", "BR");
  }

  @Test
  void entityToResponseShouldReturnNullWhenMentorIsNull() {
    assertNull(mentorMapper.entityToResponse((Mentor) null));
  }

  @Test
  void entityToResponseShouldMapAllFieldsIncludingTotalClasses() {
    mentor.getClasses().add(new Lesson());
    mentor.getClasses().add(new Lesson());

    MentorModelResponse response = mentorMapper.entityToResponse(mentor);

    assertEquals(mentor.getId(), response.getId());
    assertEquals("Grace", response.getName());
    assertEquals("Hopper", response.getLastName());
    assertEquals("mentor@example.com", response.getEmail());
    assertEquals(LocalDate.of(1906, 12, 9), response.getBirthDate());
    assertEquals("@grace", response.getSocialMedias());
    assertEquals("COBOL inventor", response.getDescription());
    assertEquals("NY", response.getState());
    assertEquals("US", response.getNationality());
    assertEquals(2, response.getTotalClasses());
  }

  @Test
  void entityToResponseShouldDefaultTotalClassesWhenListIsNull() {
    mentor.setClasses(null);

    MentorModelResponse response = mentorMapper.entityToResponse(mentor);

    assertEquals(0, response.getTotalClasses());
  }

  @Test
  void entityListToResponseShouldMapEveryMentor() {
    Mentor other = new Mentor();
    AuthUser otherUser = new AuthUser();
    otherUser.setEmail("other@example.com");
    other.setId(UUID.randomUUID());
    other.setUser(otherUser);
    other.setName("Alan");
    other.setLastName("Turing");

    List<MentorModelResponse> responses = mentorMapper.entityToResponse(List.of(mentor, other));

    assertEquals(2, responses.size());
    assertEquals("Grace", responses.get(0).getName());
    assertEquals("Alan", responses.get(1).getName());
  }

  @Test
  void updateEntityFromRequestShouldUpdateOnlyProvidedFields() {
    mentorMapper.updateEntityFromRequest(mentor, updateRequest);

    assertEquals("Ada", mentor.getName());
    assertEquals("Lovelace", mentor.getLastName());
    assertEquals(LocalDate.of(1815, 12, 10), mentor.getBirthDate());
    assertEquals("@ada", mentor.getSocialMedias());
    assertEquals("Analytical Engine", mentor.getDescription());
    assertEquals("RJ", mentor.getState());
    assertEquals("BR", mentor.getNationality());
  }

  @Test
  void updateEntityFromRequestShouldIgnoreNullFields() {
    MentorUpdateModelRequest partial =
        new MentorUpdateModelRequest(null, null, null, "@partial", "Partial desc", "MG", "PT");

    mentorMapper.updateEntityFromRequest(mentor, partial);

    assertEquals("Grace", mentor.getName());
    assertEquals("Hopper", mentor.getLastName());
    assertEquals(LocalDate.of(1906, 12, 9), mentor.getBirthDate());
    assertEquals("@partial", mentor.getSocialMedias());
    assertEquals("Partial desc", mentor.getDescription());
    assertEquals("MG", mentor.getState());
    assertEquals("PT", mentor.getNationality());
  }

  @Test
  void entityToInfoResponseShouldAggregateMentorDetails() {
    List<MentorReviewResponse> reviews =
        List.of(
            new MentorReviewResponse(
                UUID.randomUUID(),
                mentor.getId(),
                UUID.randomUUID(),
                "Mentored",
                5,
                4,
                3,
                5,
                4,
                "Great"),
            new MentorReviewResponse(
                UUID.randomUUID(),
                mentor.getId(),
                UUID.randomUUID(),
                "Another",
                4,
                4,
                4,
                4,
                4,
                "Good"));

    MentorInfoModelResponse response = mentorMapper.entityToInfoResponse(mentor, 7, true, reviews);

    assertEquals(mentor.getId(), response.getId());
    assertEquals("Grace Hopper", response.getName());
    assertEquals("NY", response.getState());
    assertEquals("US", response.getNationality());
    assertEquals("@grace", response.getSocialMedias());
    assertEquals("COBOL inventor", response.getDescription());
    assertEquals(7, response.getTotalClasses());
    assertTrue(response.getCanAddReview());
    assertEquals(reviews, response.getReviews());
  }
}
