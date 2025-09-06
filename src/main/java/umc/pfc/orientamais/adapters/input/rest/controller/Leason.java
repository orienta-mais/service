package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LeasonModelResponse;
import umc.pfc.orientamais.application.port.input.LeasonUseCase;

@RestController
@RequestMapping("/leason")
@RequiredArgsConstructor
public class Leason {

    private final ModelMapper mapper;
    private final LeasonUseCase leasonUseCase;

    @PostMapping("/create-leason")
    public ResponseEntity<GenericModelResponse> createLeason(@Valid @RequestBody CreateLeasonModelRequest request) {
        try {
            GenericModelResponse reponse = leasonUseCase.createLeason(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error creating leason: " + e.getMessage()));
        }
    }

    @GetMapping("/list-leason-by-id")
    public ResponseEntity<?> listLeasonById(@Valid @RequestBody ListLeasonByIdModelRequest request) {
        try {
            var reponse = leasonUseCase.listLeasonById(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error listing leason: " + e.getMessage()));
        }
    }

    @PutMapping("/update-leason")
    public ResponseEntity<GenericModelResponse> updateLeason(@Valid @RequestBody UpdateLeasonModelRequest request) {
        try {
            GenericModelResponse reponse = leasonUseCase.updateLeason(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error updating leason: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-leason")
    public ResponseEntity<GenericModelResponse> deleteLeason(@Valid @RequestBody DeleteLeasonModelRequest request) {
        try {
            GenericModelResponse reponse = leasonUseCase.deleteLeason(request);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(reponse);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GenericModelResponse("ERROR", "Error deleting leason: " + e.getMessage()));
        }
    }
}
