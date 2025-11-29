package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.PresenceCodeModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonMentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.application.port.input.CertificateUseCase;
import umc.pfc.orientamais.application.service.email.EmailSenderService;
import umc.pfc.orientamais.application.service.utils.CertificatePdfGenerator;
import umc.pfc.orientamais.application.service.utils.SecurityUtils;
import umc.pfc.orientamais.domain.exceptions.BadRequestException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateService implements CertificateUseCase {

    private final LessonRepository lessonRepository;
    private final LessonMentoredRepository lessonMentoredRepository;
    private final MentoredRepository mentoredRepository;
    private final CertificatePdfGenerator pdfGenerator;
    private final EmailSenderService emailSender;

    @Override
    public GenericModelResponse validatePresenceAndGenerateCertificate(UUID lessonId, PresenceCodeModelRequest request) {
        UUID mentoredAuthId = SecurityUtils.getCurrentProfileId();
        Mentored mentored = mentoredRepository.findByUserId(mentoredAuthId)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Aula não encontrada"));

        LessonMentored lessonMentored = lessonMentoredRepository
                .findByLessonIdAndMentoredId(lessonId, mentored.getId())
                .orElseThrow(() -> new BadRequestException("Você não está inscrito nesta aula"));

        if (!lesson.getPresentCode().equals(request.code())) {
            throw new BadRequestException("Código de presença incorreto");
        }

        if (lessonMentored.isCertificateGenerated()) {
            throw new BadRequestException("O certificado já foi gerado anteriormente");
        }

        lesson.setPresentCodeFilled(true);
        lessonRepository.save(lesson);
        byte[] pdf = pdfGenerator.generateCertificate(mentored, lesson);

        lessonMentored.setCertificateGenerated(true);
        lessonMentored.setCertificateGeneratedAt(LocalDateTime.now());
        lessonMentoredRepository.save(lessonMentored);

        String subject = "Certificado de participação - " + lesson.getTitle();
        String content = "<p>Olá, " + mentored.getName() + "!</p>"
                + "<p>Segue em anexo seu certificado de participação na aula <strong>" + lesson.getTitle() + "</strong>.</p>"
                + "<p>Equipe Orienta+</p>";

        emailSender.sendEmailWithAttachment(
                mentored.getUser().getEmail(),
                subject,
                content,
                "certificado.pdf",
                pdf
        );

        return new GenericModelResponse(
                "CERTIFICATE_GENERATED",
                "Certificado gerado e enviado com sucesso!"
        );
    }

    @Override
    public GenericModelResponse regenerateCertificate(UUID lessonId) {
        UUID mentoredAuthId = SecurityUtils.getCurrentProfileId();
        Mentored mentored = mentoredRepository.findByUserId(mentoredAuthId)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Aula não encontrada"));

        LessonMentored lessonMentored = lessonMentoredRepository
                .findByLessonIdAndMentoredId(lessonId, mentored.getId())
                .orElseThrow(() -> new BadRequestException("Você não participou desta aula"));

        if (!lessonMentored.isCertificateGenerated()) {
            throw new BadRequestException("O certificado ainda não foi gerado. Valide o código de presença primeiro");
        }

        byte[] pdf = pdfGenerator.generateCertificate(mentored, lesson);
        String subject = "Reemissão de certificado - " + lesson.getTitle();
        String content = "<p>Olá, " + mentored.getName() + "!</p>"
                + "<p>Segue novamente seu certificado da aula <strong>" + lesson.getTitle() + "</strong>.</p>"
                + "<p>Equipe Orienta+</p>";

        emailSender.sendEmailWithAttachment(
                mentored.getUser().getEmail(),
                subject,
                content,
                "certificado.pdf",
                pdf
        );

        return new GenericModelResponse(
                "CERTIFICATE_REGENERATED",
                "Certificado reenviado com sucesso!"
        );
    }
}
