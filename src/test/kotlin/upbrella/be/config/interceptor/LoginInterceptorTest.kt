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
import upbrella.be.user.repository.UserRepository
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ExtendWith(MockitoExtension::class)
class LoginInterceptorTest {

    @Mock
    private lateinit var httpServletRequest: HttpServletRequest

    @Mock
    private lateinit var httpServletResponse: HttpServletResponse

    @Mock
    private lateinit var requestDispatcher: RequestDispatcher

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var handler: Any

    @InjectMocks
    private lateinit var loginInterceptor: LoginInterceptor

    @Test
    @DisplayName("세션이 없는 경우 false를 반환한다.")
    fun noSession() {
        // given
        given(httpServletRequest.getSession(false))
            .willReturn(null)
        given(httpServletRequest.getRequestDispatcher(any()))
            .willReturn(requestDispatcher)
        willDoNothing().given(requestDispatcher)
            .forward(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java))

        // when
        val result = loginInterceptor.preHandle(httpServletRequest, httpServletResponse, handler)

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
    @DisplayName("로그인된 세션이 없는 경우 false를 반환한다.")
    fun notLogined() {
        // given
        val mockHttpSession = MockHttpSession()

        given(httpServletRequest.getSession(false))
            .willReturn(mockHttpSession)
        given(httpServletRequest.getRequestDispatcher(any()))
            .willReturn(requestDispatcher)
        willDoNothing().given(requestDispatcher)
            .forward(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java))

        // when
        val result = loginInterceptor.preHandle(httpServletRequest, httpServletResponse, handler)

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
    @DisplayName("세션의 유저와 일치하는 회원이 없는 경우 false를 반환한다.")
    fun nonMember() {
        // given
        val mockHttpSession = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        mockHttpSession.setAttribute("user", sessionUser)

        given(httpServletRequest.getSession(false))
            .willReturn(mockHttpSession)
        given(httpServletRequest.getRequestDispatcher(any()))
            .willReturn(requestDispatcher)
        given(userRepository.existsById(sessionUser.getId()))
            .willReturn(false)
        willDoNothing().given(requestDispatcher)
            .forward(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java))

        // when
        val result = loginInterceptor.preHandle(httpServletRequest, httpServletResponse, handler)

        // then
        assertAll(
            { assertThat(result).isEqualTo(false) },
            { then(httpServletRequest).should(times(1)).getSession(false) },
            { then(userRepository).should(times(1)).existsById(sessionUser.getId()) },
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
    @DisplayName("인가를 받은 세션이 있는 경우 true를 반환한다.")
    fun success() {
        // given
        val mockHttpSession = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        mockHttpSession.setAttribute("user", sessionUser)

        given(httpServletRequest.getSession(false))
            .willReturn(mockHttpSession)
        given(userRepository.existsById(sessionUser.getId()))
            .willReturn(true)

        // when
        val result = loginInterceptor.preHandle(httpServletRequest, httpServletResponse, handler)

        // then
        assertAll(
            { assertThat(result).isEqualTo(true) },
            { then(httpServletRequest).should(times(1)).getSession(false) },
            { then(userRepository).should(times(1)).existsById(sessionUser.getId()) }
        )
    }
}
