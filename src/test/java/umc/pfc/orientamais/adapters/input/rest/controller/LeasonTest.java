package umc.pfc.orientamais.adapters.input.rest.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.DeleteLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ListLeasonByIdModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdateLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LeasonModelResponse;
import umc.pfc.orientamais.application.port.input.LeasonUseCase;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LeasonTest {

    @InjectMocks
    private Leason leasonController;

    @Mock
    private LeasonUseCase leasonUseCase;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createLeason_success() {
        CreateLeasonModelRequest request = new CreateLeasonModelRequest();
        GenericModelResponse mockResponse = new GenericModelResponse("SUCCESS", "Leason created");
        when(leasonUseCase.createLeason(request)).thenReturn(mockResponse);

        var responseEntity = leasonController.createLeason(request);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
        verify(leasonUseCase, times(1)).createLeason(request);
    }

    @Test
    void createLeason_failure() {
        CreateLeasonModelRequest request = new CreateLeasonModelRequest();
        when(leasonUseCase.createLeason(request)).thenThrow(new RuntimeException("DB error"));

        var responseEntity = leasonController.createLeason(request);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals("ERROR", responseEntity.getBody().getCode());
        assertTrue(responseEntity.getBody().getMessage().contains("DB error"));
        verify(leasonUseCase, times(1)).createLeason(request);
    }

    @Test
    void listLeasonById_success() {
        ListLeasonByIdModelRequest request = new ListLeasonByIdModelRequest();
        var mockResponse = new LeasonModelResponse();
        mockResponse.setId(UUID.randomUUID());
        mockResponse.setTitle("Aula de Java Avançado");
        mockResponse.setDescription("Nesta aula vamos aprofundar conceitos de Streams e Concurrency.");
        mockResponse.setLink("https://meet.example.com/java-avancado");
        mockResponse.setMaxGuest(20);
        mockResponse.setStartTime(LocalDateTime.of(2025, 9, 20, 14, 0));
        mockResponse.setEndTime(LocalDateTime.of(2025, 9, 20, 16, 0));
        mockResponse.setPresentCode("JAVA2025");
        when(leasonUseCase.listLeasonById(request)).thenReturn(mockResponse);

        var responseEntity = leasonController.listLeasonById(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
        verify(leasonUseCase, times(1)).listLeasonById(request);
    }

    @Test
    void listLeasonById_failure() {
        ListLeasonByIdModelRequest request = new ListLeasonByIdModelRequest();
        when(leasonUseCase.listLeasonById(request)).thenThrow(new RuntimeException("DB error"));

        var responseEntity = leasonController.listLeasonById(request);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals("ERROR", ((GenericModelResponse) responseEntity.getBody()).getCode());
        assertTrue(((GenericModelResponse) responseEntity.getBody()).getMessage().contains("DB error"));
        verify(leasonUseCase, times(1)).listLeasonById(request);
    }

    @Test
    void updateLeason_success() {
        UpdateLeasonModelRequest request = new UpdateLeasonModelRequest();
        GenericModelResponse mockResponse = new GenericModelResponse("SUCCESS", "Leason updated");
        when(leasonUseCase.updateLeason(request)).thenReturn(mockResponse);

        var responseEntity = leasonController.updateLeason(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
        verify(leasonUseCase, times(1)).updateLeason(request);
    }

    @Test
    void updateLeason_failure() {
        UpdateLeasonModelRequest request = new UpdateLeasonModelRequest();
        when(leasonUseCase.updateLeason(request)).thenThrow(new RuntimeException("DB error"));

        var responseEntity = leasonController.updateLeason(request);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals("ERROR", responseEntity.getBody().getCode());
        assertTrue(responseEntity.getBody().getMessage().contains("DB error"));
        verify(leasonUseCase, times(1)).updateLeason(request);
    }

    @Test
    void deleteLeason_success() {
        DeleteLeasonModelRequest request = new DeleteLeasonModelRequest();
        GenericModelResponse mockResponse = new GenericModelResponse("SUCCESS", "Leason deleted");
        when(leasonUseCase.deleteLeason(request)).thenReturn(mockResponse);

        var responseEntity = leasonController.deleteLeason(request);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
        verify(leasonUseCase, times(1)).deleteLeason(request);
    }

    @Test
    void deleteLeason_failure() {
        DeleteLeasonModelRequest request = new DeleteLeasonModelRequest();
        when(leasonUseCase.deleteLeason(request)).thenThrow(new RuntimeException("DB error"));

        var responseEntity = leasonController.deleteLeason(request);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals("ERROR", responseEntity.getBody().getCode());
        assertTrue(responseEntity.getBody().getMessage().contains("DB error"));
        verify(leasonUseCase, times(1)).deleteLeason(request);
    }
}
