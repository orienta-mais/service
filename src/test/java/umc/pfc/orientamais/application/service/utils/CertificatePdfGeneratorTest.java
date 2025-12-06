package umc.pfc.orientamais.application.service.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@ExtendWith(MockitoExtension.class)
class CertificatePdfGeneratorTest {

  @InjectMocks private CertificatePdfGenerator certificatePdfGenerator;

  private Mentored mentored;
  private Lesson lesson;

  @BeforeEach
  void setUp() {
    mentored = new Mentored();
    mentored.setId(UUID.randomUUID());
    mentored.setName("Aluno");
    mentored.setLastName("Teste");

    Mentor mentor = new Mentor();
    mentor.setName("Mentor Nome");

    lesson = new Lesson();
    lesson.setId(UUID.randomUUID());
    lesson.setMentor(mentor);
    lesson.setTitle("Título");
    lesson.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
    lesson.setEndTime(LocalDateTime.of(2025, 1, 1, 12, 0));
  }

  @Test
  void generateCertificateShouldReturnSignedPdf() {
    byte[] pdf = certificatePdfGenerator.generateCertificate(mentored, lesson);

    assertNotNull(pdf);
    assertTrue(pdf.length > 0);
  }
}
