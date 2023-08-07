package upbrella.be.docs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup(RestDocumentationContextProvider provider) {

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .apply(documentationConfiguration(provider))
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static MockMvc setControllerAdvice(Object initController, Object controllerAdvice) {

        return MockMvcBuilders.standaloneSetup(initController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(controllerAdvice)
                .build();
    }

    protected abstract Object initController();
}
