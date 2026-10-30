package upbrella.be.config.interceptor

import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.repository.UserRepository

class LoginInterceptorTest {
    private val users = mock<UserRepository>()
    private val interceptor = LoginInterceptor(users)
    @Test fun `missing session returns HTTP 401`() {
        val request=MockHttpServletRequest();val response=MockHttpServletResponse()
        assertThat(interceptor.preHandle(request,response,Any())).isFalse()
        assertThat(response.status).isEqualTo(401)
        assertThat(response.contentAsString).contains("unauthorized")
    }
    @Test fun `social session without application login returns HTTP 401`() {
        val request=MockHttpServletRequest();request.getSession(true)!!.setAttribute("kakaoUser","social-only")
        val response=MockHttpServletResponse()
        assertThat(interceptor.preHandle(request,response,Any())).isFalse();assertThat(response.status).isEqualTo(401)
    }
    @Test fun `deleted member cannot reuse a persisted session`() {
        val request=MockHttpServletRequest();request.getSession(true)!!.setAttribute("user",SessionUser(1))
        val response=MockHttpServletResponse();whenever(users.existsById(1)).thenReturn(false)
        assertThat(interceptor.preHandle(request,response,Any())).isFalse();assertThat(response.status).isEqualTo(401)
    }
    @Test fun `valid persisted member session is accepted`() {
        val request=MockHttpServletRequest();request.getSession(true)!!.setAttribute("user",SessionUser(1))
        val response=MockHttpServletResponse();whenever(users.existsById(1)).thenReturn(true)
        assertThat(interceptor.preHandle(request,response,Any())).isTrue();assertThat(request.getAttribute("user")).isEqualTo(SessionUser(1))
    }
}
