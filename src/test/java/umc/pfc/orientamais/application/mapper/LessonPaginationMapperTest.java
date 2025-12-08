package umc.pfc.orientamais.application.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import umc.pfc.orientamais.adapters.input.rest.dto.response.PagedModelResponse;

@ExtendWith(MockitoExtension.class)
class LessonPaginationMapperTest {

  @InjectMocks private LessonPaginationMapper lessonPaginationMapper;

  private MockHttpServletRequest request;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest("GET", "/lessons");
    request.setServerName("localhost");
    request.setScheme("http");
    request.setServerPort(8080);
  }

  @AfterEach
  void cleanContext() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void toPagedModelShouldPopulateMetadataAndNavigationLinks() {
    request.setQueryString("title=java&page=1");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    List<String> content = new ArrayList<>(List.of("lesson-1", "lesson-2"));
    Page<String> page = new PageImpl<>(content, PageRequest.of(1, 2), 5);

    PagedModelResponse<String> response = lessonPaginationMapper.toPagedModel(page);

    assertEquals(content, response.getContent());
    assertEquals(2, response.getSize());
    assertEquals(5, response.getTotal());
    assertEquals(3, response.getTotalPages());
    assertEquals(1, response.getCurrentPage());
    assertEquals("http://localhost/lessons?title=java&page=2", response.getNextPage());
    assertEquals("http://localhost/lessons?title=java&page=0", response.getPreviousPage());
    assertThrows(UnsupportedOperationException.class, () -> response.getContent().add("blocked"));
  }

  @Test
  void toPagedModelShouldReturnNullNavigationWhenBoundaryReached() {
    request.setQueryString(null);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    Page<String> page = new PageImpl<>(List.of("single"), PageRequest.of(0, 5), 1);

    PagedModelResponse<String> response = lessonPaginationMapper.toPagedModel(page);

    assertNull(response.getNextPage());
    assertNull(response.getPreviousPage());
    assertEquals(1, response.getTotal());
    assertEquals(1, response.getTotalPages());
    assertEquals(0, response.getCurrentPage());
  }
}
