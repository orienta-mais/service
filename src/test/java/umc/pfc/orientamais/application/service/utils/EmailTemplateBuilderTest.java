package umc.pfc.orientamais.application.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailTemplateBuilderTest {

    private ResourceLoader resourceLoader;
    private EmailTemplateBuilder emailTemplateBuilder;
    private Resource resource;

    @BeforeEach
    void setUp() {
        resourceLoader = mock(ResourceLoader.class);
        resource = mock(Resource.class);
        emailTemplateBuilder = new EmailTemplateBuilder(resourceLoader);
    }

    @Test
    void shouldBuildMentorRegisterEmailSuccessfully() throws IOException {
        String templateContent = "<html>Confirme seu cadastro: ${link}</html>";
        when(resourceLoader.getResource("classpath:templates/mentor_register.html")).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(templateContent.getBytes(StandardCharsets.UTF_8)));

        String link = "http://teste.com/confirm";
        String result = emailTemplateBuilder.buildMentorRegisterEmail(link);

        assertTrue(result.contains(link));
        assertFalse(result.contains("${link}"));
        assertEquals("<html>Confirme seu cadastro: http://teste.com/confirm</html>", result);
    }

    @Test
    void shouldThrowInternalErrorExceptionWhenIOExceptionOccurs() throws IOException {
        when(resourceLoader.getResource("classpath:templates/mentor_register.html")).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(new IOException("Falha ao ler"));

        InternalErrorException exception = assertThrows(
                InternalErrorException.class,
                () -> emailTemplateBuilder.buildMentorRegisterEmail("http://teste.com/confirm")
        );

        assertEquals("Erro ao carregar template de e-mail", exception.getMessage());
    }
}
