package umc.pfc.orientamais.application.mapper;

import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import umc.pfc.orientamais.adapters.input.rest.dto.response.PagedModelResponse;

@Component
public class LessonPaginationMapper {

  public <T> PagedModelResponse<T> toPagedModel(Page<T> page) {
    PagedModelResponse<T> paged = new PagedModelResponse<>();
    paged.setContent(Collections.unmodifiableList(page.getContent()));
    paged.setSize(page.getSize());
    paged.setTotal(page.getTotalElements());
    paged.setTotalPages(page.getTotalPages());
    paged.setCurrentPage(page.getNumber());

    String baseUri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .replaceQueryParam("page")
            .build()
            .toUriString();

    paged.setNextPage(
        page.hasNext()
            ? UriComponentsBuilder.fromUriString(baseUri)
                .replaceQueryParam("page", page.getNumber() + 1)
                .build()
                .toUriString()
            : null);

    paged.setPreviousPage(
        page.hasPrevious()
            ? UriComponentsBuilder.fromUriString(baseUri)
                .replaceQueryParam("page", page.getNumber() - 1)
                .build()
                .toUriString()
            : null);

    return paged;
  }
}
