package upbrella.be.error.controller

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import upbrella.be.docs.utils.RestDocsSupport

class ErrorControllerTest : RestDocsSupport() {
    override fun initController(): Any {
        return ErrorController()
    }

    @Test
    @DisplayName("예외 컨트롤러에 포워딩된 요청은 400 에러를 반환한다.")
    fun getError() {
        mockMvc.perform(
            get("/api/error")
        ).andExpect(status().isBadRequest)
    }
}