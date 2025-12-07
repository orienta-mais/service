package umc.pfc.orientamais.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorReviewRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class MentorReviewMapperTest {

    @InjectMocks
    private MentorReviewMapper mentorReviewMapper;

    private MentorReviewRequest request;
    private UUID mentorId;
    private UUID mentoredId;

    @BeforeEach
    void setUp() {
        request = new MentorReviewRequest(5, 4, 3, 2, 1, "Great mentor");
        mentorId = UUID.randomUUID();
        mentoredId = UUID.randomUUID();
    }

    @Test
    void toEntityShouldPopulateAllFields() {
        UUID generatedId = UUID.randomUUID();
        try (MockedStatic<UUID> uuidMock = mockStatic(UUID.class)) {
            uuidMock.when(UUID::randomUUID).thenReturn(generatedId);

            MentorReview entity = mentorReviewMapper.toEntity(request, mentorId, mentoredId);

            assertEquals(generatedId, entity.getId());
            assertEquals(mentorId, entity.getMentorId());
            assertEquals(mentoredId, entity.getMentoredId());
            assertEquals(5, entity.getDidactics());
            assertEquals(4, entity.getSubjectMastery());
            assertEquals(3, entity.getPunctuality());
            assertEquals(2, entity.getCommunication());
            assertEquals(1, entity.getEngagement());
            assertEquals("Great mentor", entity.getFeedback());
        }
    }

    @Test
    void toResponseWithoutMentoredNameShouldReturnRecord() {
        MentorReview entity = buildReviewEntity();

        MentorReviewResponse response = mentorReviewMapper.toResponse(entity);

        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getMentorId(), response.mentorId());
        assertEquals(entity.getMentoredId(), response.mentoredId());
        assertNull(response.mentoredName());
        assertEquals(entity.getDidactics(), response.didactics());
        assertEquals(entity.getSubjectMastery(), response.subjectMastery());
        assertEquals(entity.getPunctuality(), response.punctuality());
        assertEquals(entity.getCommunication(), response.communication());
        assertEquals(entity.getEngagement(), response.engagement());
        assertEquals(entity.getFeedback(), response.feedback());
    }

    @Test
    void toResponseWithMentoredNameShouldIncludeName() {
        MentorReview entity = buildReviewEntity();

        MentorReviewResponse response = mentorReviewMapper.toResponse(entity, "Mentored Name");

        assertEquals("Mentored Name", response.mentoredName());
    }

    private MentorReview buildReviewEntity() {
        MentorReview entity = new MentorReview();
        entity.setId(UUID.randomUUID());
        entity.setMentorId(mentorId);
        entity.setMentoredId(mentoredId);
        entity.setDidactics(5);
        entity.setSubjectMastery(4);
        entity.setPunctuality(3);
        entity.setCommunication(2);
        entity.setEngagement(1);
        entity.setFeedback("Feedback");
        return entity;
    }
}

