package umc.pfc.orientamais.application.service.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class EmailTemplateBuilder {

    private final ResourceLoader resourceLoader;

    public EmailTemplateBuilder(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String buildMentorRegisterEmail(String link) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/mentor_register.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("${link}", link);
        } catch (IOException e) {
            throw new InternalErrorException("Erro ao carregar template de e-mail");
        }
    }

    public String buildPasswordResetEmail(String link) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/password_reset.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("${link}", link);
        } catch (IOException e) {
            throw new InternalErrorException("Erro ao carregar template de e-mail");
        }
    }

    public String buildPasswordChangedEmail() {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/password_changed.html");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InternalErrorException("Erro ao carregar template de e-mail");
        }
    }
}
