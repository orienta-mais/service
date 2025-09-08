package umc.pfc.orientamais.application.port.input;

import jakarta.validation.Valid;
import umc.pfc.orientamais.adapters.input.rest.dto.request.DeleteLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ListLeasonByIdModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdateLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLeasonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LeasonModelResponse;

public interface LeasonUseCase {
    GenericModelResponse createLeason(CreateLeasonModelRequest request);

    GenericModelResponse deleteLeason(@Valid DeleteLeasonModelRequest request);

    LeasonModelResponse listLeasonById(@Valid ListLeasonByIdModelRequest request);

    GenericModelResponse updateLeason(@Valid UpdateLeasonModelRequest request);
}
