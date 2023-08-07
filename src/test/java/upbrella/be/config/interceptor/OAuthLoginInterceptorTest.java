package upbrella.be.config.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import upbrella.be.user.repository.UserRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthLoginInterceptorTest {

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private UserRepository userRepository;

    @Mock
    private Object handler;

    @InjectMocks
    private OAuthLoginInterceptor oAuthLoginInterceptor;

    @Test
    @DisplayName("세션이 없는 경우 false를 반환한다.")
    void noSession() throws ServletException, IOException {

        given(httpServletRequest.getSession(false))
                .willReturn(null);
        given(httpServletRequest.getRequestDispatcher(any()))
                .willReturn(requestDispatcher);
        willDoNothing().given(requestDispatcher)
                .forward(any(HttpServletRequest.class), any(HttpServletResponse.class));

        boolean result = oAuthLoginInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);

        assertAll(
                () -> assertThat(result)
                        .isEqualTo(false),
                () -> then(httpServletRequest)
                        .should(times(1))
                        .getSession(false),
                () -> then(httpServletRequest)
                        .should(times(1))
                        .getRequestDispatcher("/api/error"),
                () -> then(requestDispatcher)
                        .should(times(1))
                        .forward(any(HttpServletRequest.class), any(HttpServletResponse.class))
        );

    }


    @Test
    @DisplayName("로그인된 세션이 없는 경우 false를 반환한다.")
    void notLogined() throws ServletException, IOException {

        MockHttpSession mockHttpSession = new MockHttpSession();

        given(httpServletRequest.getSession(false))
                .willReturn(mockHttpSession);
        given(httpServletRequest.getRequestDispatcher(any()))
                .willReturn(requestDispatcher);
        willDoNothing().given(requestDispatcher)
                .forward(any(HttpServletRequest.class), any(HttpServletResponse.class));

        boolean result = oAuthLoginInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);

        assertAll(
                () -> assertThat(result)
                        .isEqualTo(false),
                () -> then(httpServletRequest)
                        .should(times(1))
                        .getSession(false),
                () -> then(httpServletRequest)
                        .should(times(1))
                        .getRequestDispatcher("/api/error"),
                () -> then(requestDispatcher)
                        .should(times(1))
                        .forward(any(HttpServletRequest.class), any(HttpServletResponse.class))
        );
    }

    @Test
    @DisplayName("세션의 유저와 일치하는 회원이 없는 경우 false를 반환한다.")
    void nonMember() throws ServletException, IOException {

        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("userId", 3L);

        given(httpServletRequest.getSession(false))
                .willReturn(mockHttpSession);
        given(httpServletRequest.getRequestDispatcher(any()))
                .willReturn(requestDispatcher);
        given(userRepository.existsById(3L))
                .willReturn(false);
        willDoNothing().given(requestDispatcher)
                .forward(any(HttpServletRequest.class), any(HttpServletResponse.class));

        // when
        boolean result = oAuthLoginInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);

        // then
        assertAll(
                () -> assertThat(result)
                        .isEqualTo(false),
                () -> then(httpServletRequest)
                        .should(times(1))
                        .getSession(false),
                () -> then(userRepository)
                        .should(times(1))
                        .existsById(3L),
                () -> then(httpServletRequest)
                        .should(times(1))
                        .getRequestDispatcher("/api/error"),
                () -> then(requestDispatcher)
                        .should(times(1))
                        .forward(any(HttpServletRequest.class), any(HttpServletResponse.class))
        );
    }

    @Test
    @DisplayName("인가를 통과한다.")
    void success() throws ServletException, IOException {

        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("userId", 3L);

        given(httpServletRequest.getSession(false))
                .willReturn(mockHttpSession);
        given(userRepository.existsById(3L))
                .willReturn(true);

        // when
        boolean result = oAuthLoginInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);

        // then
        assertAll(
                () -> assertThat(result)
                        .isEqualTo(true),
                () -> then(httpServletRequest)
                        .should(times(1))
                        .getSession(false),
                () -> then(userRepository)
                        .should(times(1))
                        .existsById(3L)
        );
    }
}