package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonMentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.application.port.output.calendar.CalendarPort;
import umc.pfc.orientamais.application.port.output.zoom.CreateMeetingPort;
import umc.pfc.orientamais.application.service.utils.SecurityUtils;
import umc.pfc.orientamais.domain.exceptions.BadRequestException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.Lesson;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;
import umc.pfc.orientamais.domain.model.clazz.LessonMentoredId;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class LessonService implements LessonUseCase {

    private static final String lessonNotFoundMessage = "Lesson não encontrado";
    private final LessonRepository lessonRepository;
    private final MentorRepository mentorRepository;
    private final MentoredRepository mentoredRepository;
    private final LessonMentoredRepository lessonMentoredRepository;
    private final ModelMapper mapper;
    private final LessonMapper lessonMapper;
    private final CalendarPort calendarPort;
    private final CreateMeetingPort createMeetingPort;

    @Override
    public GenericModelResponse createLesson(CreateLessonModelRequest request) {
        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado."));

        var meetingUrl = createMeetingPort.returnMeetingUrl();

        Lesson lesson = lessonMapper.requestToEntity(request);
        lesson.setLink(meetingUrl);
        lesson.setMentor(mentor);

        lessonRepository.save(lesson);

        try {
            List<String> attendees = Collections.singletonList(mentor.getUser().getEmail());
            String externalEventId = calendarPort.createEvent(lesson, attendees);
            lesson.setExternalEventId(externalEventId);
            lessonRepository.save(lesson);

        } catch (Exception ex) {
            System.err.println("Erro ao enviar convite da aula: " + ex.getMessage());
        }

        return new GenericModelResponse("lesson_CREATED", "Aula criada e convite enviado com sucesso!");
    }

    @Override
    public GenericModelResponse deleteLesson(String request) {
        UUID lessonId = UUID.fromString(request);
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));
        lessonRepository.deleteById(lessonId);

        return new GenericModelResponse("LESSON_DELETED", "Lesson deleted successfully!");
    }

    @Override
    public LessonModelResponse listLessonById(String request) {
        UUID lessonId = UUID.fromString(request);
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));
        return lessonMapper.entityToResponse(lesson);
    }

    @Override
    public GenericModelResponse updateLesson(String lessonId, UpdatelessonModelRequest request) {
        UUID lessonIdParsed = UUID.fromString(lessonId);
        var lesson = lessonMapper.requestToEntity(request, lessonIdParsed);
        lessonRepository.findById(lessonIdParsed)
                .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));
        mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        lessonRepository.save(lesson);

        return new GenericModelResponse("LESSON_UPDATED", "Lesson updated successfully!");
    }

    @Override
    public List<LessonModelResponse> listLessonByMentorId(UUID request) {
        var lessons = lessonRepository.findByMentorId(request);
        return lessonMapper.entityToResponse(lessons);
    }

    @Override
    public List<LessonModelResponse> listLessonByMentoredId(UUID request) {
        List<Lesson> lessons = new ArrayList<>();
        List<LessonMentored> lessonsMentored = lessonMentoredRepository.findByMentoredId(request);
        lessonsMentored.forEach(lessonMentored -> {
            lessons.add(lessonMentored.getLesson());
        });
        return lessonMapper.entityToResponse(lessons);
    }

    @Override
    @Transactional
    public GenericModelResponse registerMentored(String lessonId) {
        UUID lessonUUID = UUID.fromString(lessonId);

        Lesson lesson = lessonRepository.findById(lessonUUID)
                .orElseThrow(() -> new NotFoundException("Aula não encontrada"));

        UUID mentoredAuthUserUUID = SecurityUtils.getCurrentProfileId();

        Mentored mentored = mentoredRepository.findByUserId(mentoredAuthUserUUID)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));

        boolean alreadyRegistered = lessonMentoredRepository.existsByLessonIdAndMentoredId(lessonUUID, mentored.getId());
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
            System.err.println("Erro ao enviar convite para o mentorado: " + ex.getMessage());
        }

        return new GenericModelResponse("MENTORED_REGISTERED", "Inscrição realizada e convite enviado com sucesso!");
    }


    @Override
    public List<LessonModelResponse> listLesson(String title, LocalDate date, String order) {
        Specification<Lesson> spec = (root, query, cb) -> cb.conjunction();

        if (title != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%")
            );
        }

        if (date != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.function("DATE", LocalDate.class, root.get("startTime")), date)
            );
        }
        Specification<Lesson> orderBySpec = (root, query, cb) -> {
            if ("desc".equalsIgnoreCase(order)) {
                query.orderBy(cb.desc(root.get("startTime")));
            } else {
                query.orderBy(cb.asc(root.get("startTime")));
            }
            return null;
        };

        spec = spec.and(orderBySpec);

        var lessons = lessonRepository.findAll(spec);
        return lessonMapper.entityToResponse(lessons);
    }
}
