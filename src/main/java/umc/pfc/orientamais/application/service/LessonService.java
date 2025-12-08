package umc.pfc.orientamais.application.service;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.*;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonMentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.application.mapper.LessonMapper;
import umc.pfc.orientamais.application.mapper.LessonPaginationMapper;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.application.port.output.calendar.CalendarPort;
import umc.pfc.orientamais.application.port.output.zoom.CreateMeetingPort;
import umc.pfc.orientamais.application.service.utils.SecurityUtils;
import umc.pfc.orientamais.application.service.utils.TimeUtils;
import umc.pfc.orientamais.domain.exceptions.BadRequestException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;
import umc.pfc.orientamais.domain.model.clazz.LessonMentoredId;
import umc.pfc.orientamais.domain.model.clazz.LessonStatus;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@Service
@Transactional
@AllArgsConstructor
public class LessonService implements LessonUseCase {

  private static final String lessonNotFoundMessage = "Lesson não encontrado";
  private final LessonRepository lessonRepository;
  private final MentorRepository mentorRepository;
  private final MentoredRepository mentoredRepository;
  private final LessonMentoredRepository lessonMentoredRepository;
  private final LessonMapper lessonMapper;
  private final CalendarPort calendarPort;
  private final CreateMeetingPort createMeetingPort;
  private final LessonPaginationMapper paginationMapper;

  @Override
  public GenericModelResponse createLesson(CreateLessonModelRequest request) {
    Mentor mentor =
        mentorRepository
            .findById(request.getMentorId())
            .orElseThrow(() -> new NotFoundException("Mentor não encontrado."));

    LocalDateTime lessonStartDateTime = request.getDate().atTime(request.getStartTime());
    LocalDateTime now = TimeUtils.nowLocalDateTimeUtc();

    if (lessonStartDateTime.isBefore(now) || lessonStartDateTime.isEqual(now)) {
      throw new BadRequestException("A data e horário da aula devem ser no futuro");
    }

    var meetingUrl = createMeetingPort.returnMeetingUrl();

    Lesson lesson = lessonMapper.requestToEntity(request);
    lesson.setLink(meetingUrl);
    lesson.setMentor(mentor);
    lesson.setStatus(LessonStatus.PENDING);

    lessonRepository.save(lesson);

    try {
      if (mentor.getUser() != null) {
        List<String> attendees = Collections.singletonList(mentor.getUser().getEmail());
        String externalEventId = calendarPort.createEvent(lesson, attendees);
        lesson.setExternalEventId(externalEventId);
        lessonRepository.save(lesson);
      }
    } catch (Exception ex) {
      System.err.println("Erro ao enviar convite da aula: " + ex.getMessage());
    }

    return new GenericModelResponse("lesson_CREATED", "Aula criada e convite enviado com sucesso!");
  }

  @Override
  public GenericModelResponse deleteLesson(String request) {
    UUID lessonId = UUID.fromString(request);
    Lesson lesson =
        lessonRepository
            .findById(lessonId)
            .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));

    List<String> mentoredEmails = getMentoredEmailsByLessonId(lessonId);

    try {
      calendarPort.cancelEvent(lesson, mentoredEmails);
    } catch (Exception ex) {
      System.err.println("Erro ao enviar emails de cancelamento: " + ex.getMessage());
    }

    lessonRepository.deleteById(lessonId);

    return new GenericModelResponse("LESSON_DELETED", "Lesson deleted successfully!");
  }

  private List<String> getMentoredEmailsByLessonId(UUID lessonId) {
    List<LessonMentored> lessonMentoreds = lessonMentoredRepository.findAllByLessonId(lessonId);
    return lessonMentoreds.stream()
        .map(LessonMentored::getMentored)
        .filter(mentored -> mentored.getUser() != null)
        .map(mentored -> mentored.getUser().getEmail())
        .toList();
  }

  @Override
  public LessonDetailsModelResponse listLessonById(String request) {
    AuthUserRole userRole = SecurityUtils.getCurrentUserRole();
    UUID profileId = SecurityUtils.getCurrentProfileId();
    UUID lessonId = UUID.fromString(request);

    Lesson lesson =
        lessonRepository
            .findById(lessonId)
            .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));
    return buildLessonResponseForUser(lesson, userRole, profileId);
  }

  @Override
  public GenericModelResponse updateLesson(String lessonId, UpdateLessonModelRequest request) {
    UUID lessonIdParsed = UUID.fromString(lessonId);

    lessonRepository
        .findById(lessonIdParsed)
        .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));
    mentorRepository
        .findById(request.getMentorId())
        .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));

    LocalDateTime lessonStartDateTime = request.getDate().atTime(request.getStartTime());
    LocalDateTime now = TimeUtils.nowLocalDateTimeUtc();

    if (lessonStartDateTime.isBefore(now) || lessonStartDateTime.isEqual(now)) {
      throw new BadRequestException("A data e horário da aula devem ser no futuro");
    }

    var lesson = lessonMapper.requestToEntity(request, lessonIdParsed);
    lessonRepository.save(lesson);

    return new GenericModelResponse("LESSON_UPDATED", "Lesson updated successfully!");
  }

  @Override
  public List<LessonDetailsModelResponse> listLessonByMentorId(UUID request) {
    var lessons = lessonRepository.findByMentorId(request);
    return lessonMapper.entityToDetailsResponse(lessons);
  }

  @Override
  public List<LessonDetailsModelResponse> listLessonByMentoredId(UUID request) {
    List<Lesson> lessons = new ArrayList<>();
    List<LessonMentored> lessonsMentored = lessonMentoredRepository.findByMentoredId(request);
    lessonsMentored.forEach(lessonMentored -> lessons.add(lessonMentored.getLesson()));
    return lessonMapper.entityToDetailsResponse(lessons);
  }

  @Override
  @Transactional
  public GenericModelResponse registerMentored(String lessonId) {
    UUID lessonUUID = UUID.fromString(lessonId);

    Lesson lesson =
        lessonRepository
            .findById(lessonUUID)
            .orElseThrow(() -> new NotFoundException("Aula não encontrada"));

    UUID mentoredAuthUserUUID = SecurityUtils.getCurrentProfileId();

    Mentored mentored =
        mentoredRepository
            .findByUserId(mentoredAuthUserUUID)
            .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));

    boolean alreadyRegistered =
        lessonMentoredRepository.existsByLessonIdAndMentoredId(lessonUUID, mentored.getId());
    if (alreadyRegistered) {
      throw new BadRequestException("Você já está inscrito nesta aula");
    }

    long totalRegistered = lessonMentoredRepository.countByLessonId(lessonUUID);
    if (lesson.getMaxGuest() != null && totalRegistered >= lesson.getMaxGuest()) {
      throw new BadRequestException("A aula já atingiu o número máximo de participantes");
    }

    LessonMentored relation = new LessonMentored();
    LessonMentoredId id = new LessonMentoredId();
    id.setLessonId(lessonUUID);
    id.setMentoredId(mentored.getId());
    relation.setId(id);
    relation.setLesson(lesson);
    relation.setMentored(mentored);
    lessonMentoredRepository.save(relation);

    try {
      calendarPort.sendInviteToMentored(lesson, mentored.getUser().getEmail());
    } catch (Exception ex) {
      System.err.println("Erro ao adicionar mentorado ao evento: " + ex.getMessage());
    }

    return new GenericModelResponse(
        "MENTORED_REGISTERED", "Inscrição realizada e convite enviado com sucesso!");
  }

  @Override
  public PagedModelResponse<LessonModelResponse> listLesson(
      String title, LocalDate date, String order, int page, int size) {
    AuthUserRole userRole = SecurityUtils.getCurrentUserRole();

    Pageable pageable = PageRequest.of(page, size, getSort(order));
    Specification<Lesson> spec = (root, query, cb) -> cb.conjunction();

    spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), LessonStatus.PENDING));

    spec =
        spec.and((root, query, cb) -> cb.greaterThan(root.get("startTime"), LocalDateTime.now()));

    if (title != null && !title.isBlank()) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
    }

    if (date != null) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.equal(cb.function("DATE", LocalDate.class, root.get("startTime")), date));
    }

    if (userRole == AuthUserRole.MENTORED) {
      spec = spec.and(hasAvailableSpots());
    }

    Page<Lesson> lessons = lessonRepository.findAll(spec, pageable);

    List<LessonModelResponse> responseList =
        lessons.getContent().stream().map(lessonMapper::entityToResponse).toList();

    Page<LessonModelResponse> mappedPage =
        new PageImpl<>(responseList, pageable, lessons.getTotalElements());

    return paginationMapper.toPagedModel(mappedPage);
  }

  private Sort getSort(String order) {
    return "desc".equalsIgnoreCase(order)
        ? Sort.by(Sort.Direction.DESC, "startTime")
        : Sort.by(Sort.Direction.ASC, "startTime");
  }

  private Specification<Lesson> hasAvailableSpots() {
    return (root, query, cb) -> {
      Subquery<Long> countSubquery = query.subquery(Long.class);
      Root<LessonMentored> subRoot = countSubquery.from(LessonMentored.class);
      countSubquery.select(cb.count(subRoot)).where(cb.equal(subRoot.get("lesson"), root));
      return cb.greaterThan(root.get("maxGuest"), countSubquery);
    };
  }

  private LessonDetailsModelResponse buildLessonResponseForUser(
      Lesson lesson, AuthUserRole role, UUID profileId) {
    LessonDetailsModelResponse response = lessonMapper.entityToDetailsResponse(lesson);

    UUID lessonId = lesson.getId();

    UUID mentoredId =
        (role == AuthUserRole.MENTORED)
            ? mentoredRepository.findByUserId(profileId).map(Mentored::getId).orElse(null)
            : null;

    LessonMentored lessonMentored =
        (mentoredId != null)
            ? lessonMentoredRepository
                .findByLessonIdAndMentoredId(lessonId, mentoredId)
                .orElse(null)
            : null;

    boolean isMentor = false;
    if (role == AuthUserRole.MENTOR
        && lesson.getMentor() != null
        && lesson.getMentor().getUser() != null) {
      isMentor = lesson.getMentor().getUser().getId().equals(profileId);
    }

    boolean isRegisteredMentored = (lessonMentored != null);

    response.setPresentCodeFilled(
        lessonMentored != null && Boolean.TRUE.equals(lessonMentored.getPresentCodeFilled()));

    if (!(isMentor || isRegisteredMentored)) {
      response.setLink(null);
    }

    if (role == AuthUserRole.MENTORED) {
      response.setPresentCode(null);
      if (lesson.getMentor() != null && !lesson.getMentor().getActive()) {
        response.setMentorId(null);
      }
    }

    return response;
  }

  @Override
  public CountLessonsResponse countUpcomingAndUnavailabLessons() {
    CountLessonsResponse response = new CountLessonsResponse();
    response.setCountUpcomingLessons(countUpcomingLessons());
    response.setCountUnavailableLessons(countUnavailableLessons());
    return response;
  }

  private Integer countUpcomingLessons() {
    return lessonRepository.countUpcomingLessons();
  }

  private Integer countUnavailableLessons() {
    return lessonRepository.countUnavailableLessons();
  }
}
