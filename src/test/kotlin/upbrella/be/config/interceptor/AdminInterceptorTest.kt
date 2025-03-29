package upbrella.be.config.interceptor

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpSession
import upbrella.be.config.FixtureBuilderFactory
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ExtendWith(MockitoExtension::class)
class AdminInterceptorTest {

    @Mock
    private lateinit var httpServletRequest: HttpServletRequest

    @Mock
    private lateinit var httpServletResponse: HttpServletResponse

    @Mock
    private lateinit var requestDispatcher: RequestDispatcher

    @Mock
    private lateinit var handler: Any

    @InjectMocks
    private lateinit var adminInterceptor: AdminInterceptor

    @Test
    @DisplayName("로그인된 사용자가 어드민이 아닌 경우 false를 반환한다.")
    fun isNotAdmin() {
        // given
        val mockHttpSession = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        mockHttpSession.setAttribute("user", sessionUser)

        given(httpServletRequest.getSession(false))
            .willReturn(mockHttpSession)
        given(httpServletRequest.getRequestDispatcher(any()))
            .willReturn(requestDispatcher)
        willDoNothing().given(requestDispatcher)
            .forward(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java))

        // when
        val result = adminInterceptor.preHandle(httpServletRequest, httpServletResponse, handler)

        // then
        assertAll(
            { assertThat(result).isEqualTo(false) },
            { then(httpServletRequest).should(times(1)).getSession(false) },
            { then(httpServletRequest).should(times(1)).getRequestDispatcher("/api/error") },
            {
                then(requestDispatcher).should(times(1)).forward(
                    any(HttpServletRequest::class.java),
                    any(HttpServletResponse::class.java)
                )
            }
        )
    }

    @Test
    @DisplayName("로그인된 사용자가 어드민인 경우 true를 반환한다.")
    fun isAdmin() {
        // given
        val mockHttpSession = MockHttpSession()
        val sessionUser =
            FixtureBuilderFactory.builderSessionUser().set("adminStatus", true).sample()
        mockHttpSession.setAttribute("user", sessionUser)

        given(httpServletRequest.getSession(false))
            .willReturn(mockHttpSession)

        // when
        val result = adminInterceptor.preHandle(httpServletRequest, httpServletResponse, handler)

        // then
        assertAll(
            { assertThat(result).isEqualTo(true) },
            { then(httpServletRequest).should(times(1)).getSession(false) },
            { then(httpServletRequest).should(never()).getRequestDispatcher("/api/error") },
            {
                then(requestDispatcher).should(never()).forward(
                    any(HttpServletRequest::class.java),
                    any(HttpServletResponse::class.java)
                )
            }
        )
    }
}
