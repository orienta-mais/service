package umc.pfc.orientamais.application.service.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonRepository;
import umc.pfc.orientamais.application.service.utils.TimeUtils;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.clazz.LessonStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonStatusScheduler {

  private final LessonRepository lessonRepository;

  @Scheduled(cron = "0 */1 * * * *")
  @Transactional
  public void updatePassedLessons() {
    LocalDateTime now = TimeUtils.nowLocalDateTimeUtc();

    List<Lesson> pendingLessons =
        lessonRepository.findByStatusAndStartTimeBefore(LessonStatus.PENDING, now);

    if (!pendingLessons.isEmpty()) {
      log.info("Atualizando {} aulas para status PASSED", pendingLessons.size());

      pendingLessons.forEach(lesson -> lesson.setStatus(LessonStatus.PASSED));
      lessonRepository.saveAll(pendingLessons);

      log.info("Aulas atualizadas com sucesso");
    }
  }
}
