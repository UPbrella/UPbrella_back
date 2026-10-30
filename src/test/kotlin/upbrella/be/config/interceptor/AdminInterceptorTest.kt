package upbrella.be.config.interceptor

import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.Optional
import upbrella.be.user.entity.User
import upbrella.be.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import upbrella.be.user.dto.response.SessionUser

class AdminInterceptorTest {
    private val users=mock<UserRepository>()
    private val interceptor=AdminInterceptor(users)
    @Test fun `session without member information gets HTTP 401`() {
        val request = MockHttpServletRequest()
        request.getSession(true)
        val response = MockHttpServletResponse()
        assertThat(interceptor.preHandle(request, response, Any())).isFalse()
        assertThat(response.status).isEqualTo(401)
    }
    @Test fun `member without administrator role gets HTTP 403`() {
        val request=MockHttpServletRequest();request.getSession(true)!!.setAttribute("user",SessionUser(1))
        val response=MockHttpServletResponse()
        assertThat(interceptor.preHandle(request,response,Any())).isFalse();assertThat(response.status).isEqualTo(403)
        assertThat(response.contentAsString).contains("forbidden")
    }
    @Test fun `administrator is accepted`() {
        val request=MockHttpServletRequest();request.getSession(true)!!.setAttribute("user",SessionUser(1,adminStatus=true))
        whenever(users.findById(1)).thenReturn(Optional.of(User(1,"운영진",email="",adminStatus=true,id=1)))
        assertThat(interceptor.preHandle(request,MockHttpServletResponse(),Any())).isTrue()
    }
    @Test fun `revoked administrator session is denied immediately`() {
        val request=MockHttpServletRequest();request.getSession(true)!!.setAttribute("user",SessionUser(1,adminStatus=true))
        whenever(users.findById(1)).thenReturn(Optional.of(User(1,"회원",email="",adminStatus=false,id=1)))
        val response=MockHttpServletResponse()
        assertThat(interceptor.preHandle(request,response,Any())).isFalse();assertThat(response.status).isEqualTo(403)
    }
}
