package umc.pfc.orientamais.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentoredUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountByStateResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountMentorAndMentoredByStateResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentoredModelResponse;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentoredMapperTest {

    @InjectMocks
    private MentoredMapper mentoredMapper;

    private Mentored mentored;
    private MentoredUpdateModelRequest updateRequest;

    @BeforeEach
    void setUp() {
        AuthUser user = new AuthUser();
        user.setEmail("mentored@example.com");

        mentored = new Mentored();
        mentored.setId(UUID.randomUUID());
        mentored.setUser(user);
        mentored.setName("Alice");
        mentored.setLastName("Johnson");
        mentored.setBirthDate(LocalDate.of(2000, 5, 10));
        mentored.setSocialMedias("@alice");
        mentored.setDescription("Existing description");
        mentored.setState("SP");
        mentored.setNationality("BR");

        updateRequest = new MentoredUpdateModelRequest(
                "New",
                "Name",
                LocalDate.of(2001, 6, 15),
                "@new",
                "New description",
                "RJ",
                "PT"
        );
    }

    @Test
    void entityToResponseShouldReturnNullWhenMentoredIsNull() {
        assertNull(mentoredMapper.entityToResponse((Mentored) null));
    }

    @Test
    void entityToResponseShouldMapAllFields() {
        MentoredModelResponse response = mentoredMapper.entityToResponse(mentored);

        assertEquals(mentored.getId(), response.getId());
        assertEquals("Alice", response.getName());
        assertEquals("Johnson", response.getLastName());
        assertEquals("mentored@example.com", response.getEmail());
        assertEquals(LocalDate.of(2000, 5, 10), response.getBirthDate());
        assertEquals("@alice", response.getSocialMedias());
        assertEquals("Existing description", response.getDescription());
        assertEquals("SP", response.getState());
        assertEquals("BR", response.getNationality());
    }

    @Test
    void entityListToResponseShouldReturnMappedItems() {
        Mentored other = new Mentored();
        AuthUser otherUser = new AuthUser();
        otherUser.setEmail("other@example.com");
        other.setId(UUID.randomUUID());
        other.setUser(otherUser);
        other.setName("Bob");
        other.setLastName("Smith");

        List<MentoredModelResponse> responses = mentoredMapper.entityToResponse(List.of(mentored, other));

        assertEquals(2, responses.size());
        assertEquals("Alice", responses.getFirst().getName());
        assertEquals("Bob", responses.get(1).getName());
    }

    @Test
    void entityListToResponseShouldHandleEmptyInput() {
        List<MentoredModelResponse> responses = mentoredMapper.entityToResponse(List.of());

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void updateEntityFromRequestShouldUpdateAllProvidedFields() {
        mentoredMapper.updateEntityFromRequest(mentored, updateRequest);

        assertEquals("New", mentored.getName());
        assertEquals("Name", mentored.getLastName());
        assertEquals(LocalDate.of(2001, 6, 15), mentored.getBirthDate());
        assertEquals("@new", mentored.getSocialMedias());
        assertEquals("New description", mentored.getDescription());
        assertEquals("RJ", mentored.getState());
        assertEquals("PT", mentored.getNationality());
    }

    @Test
    void updateEntityFromRequestShouldIgnoreNullBasicFieldsButApplyOptionalOnes() {
        MentoredUpdateModelRequest partialRequest = new MentoredUpdateModelRequest(
                null,
                null,
                null,
                null,
                "Changed description",
                "BA",
                "US"
        );

        mentoredMapper.updateEntityFromRequest(mentored, partialRequest);

        assertEquals("Alice", mentored.getName());
        assertEquals("Johnson", mentored.getLastName());
        assertEquals(LocalDate.of(2000, 5, 10), mentored.getBirthDate());
        assertNull(mentored.getSocialMedias());
        assertEquals("Changed description", mentored.getDescription());
        assertEquals("BA", mentored.getState());
        assertEquals("US", mentored.getNationality());
    }

    @Test
    void toCountByStateResponseShouldConvertRowsToDtos() {
        List<Object[]> rows = List.of(
                new Object[]{"SP", 5L},
                new Object[]{"RJ", 3}
        );

        CountByStateResponse response = mentoredMapper.toCountByStateResponse(rows);

        List<CountMentorAndMentoredByStateResponse> totals = response.getTotal();
        assertEquals(2, totals.size());
        assertEquals("SP", totals.get(0).getState());
        assertEquals(5, totals.get(0).getTotalRegistered());
        assertEquals("RJ", totals.get(1).getState());
        assertEquals(3, totals.get(1).getTotalRegistered());
    }
}
