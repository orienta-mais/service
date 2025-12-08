package umc.pfc.orientamais.adapters.input.rest.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagedModelResponse<T> {
  private List<T> content;
  private int size;
  private long total;
  private int totalPages;
  private int currentPage;
  private String nextPage;
  private String previousPage;
}
