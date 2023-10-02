package upbrella.be.error.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import upbrella.be.docs.utils.RestDocsSupport;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ErrorControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {

        return new ErrorController();
    }

    @Test
    @DisplayName("예외 컨트롤러에 포워딩된 요청은 400 에러를 반환한다.")
    void getError() throws Exception {

        mockMvc.perform(
                        get("/api/error"))
                .andExpect(status().isBadRequest());
    }
}