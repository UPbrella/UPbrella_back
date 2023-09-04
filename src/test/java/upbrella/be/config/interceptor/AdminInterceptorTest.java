package upbrella.be.config.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.user.dto.response.SessionUser;
import upbrella.be.user.repository.UserRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInterceptorTest {

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
    private AdminInterceptor adminInterceptor;

    @Test
    @DisplayName("로그인된 사용자가 어드민이 아닌 경우 false를 반환한다.")
    void isNotAdmin() throws Exception {

        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        mockHttpSession.setAttribute("user", sessionUser);

        given(httpServletRequest.getSession(false))
                .willReturn(mockHttpSession);
        given(httpServletRequest.getRequestDispatcher(any()))
                .willReturn(requestDispatcher);
        willDoNothing().given(requestDispatcher)
                .forward(any(HttpServletRequest.class), any(HttpServletResponse.class));

        boolean result = adminInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);

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
    @DisplayName("로그인된 사용자가 어드민인 경우 true를 반환한다.")
    void isAdmin() throws Exception {

        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().set("adminStatus", true).sample();
        mockHttpSession.setAttribute("user", sessionUser);

        given(httpServletRequest.getSession(false))
                .willReturn(mockHttpSession);

        boolean result = adminInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);

        // then
        assertAll(
                () -> assertThat(result)
                        .isEqualTo(true),
                () -> then(httpServletRequest)
                        .should(times(1))
                        .getSession(false),
                () -> then(httpServletRequest)
                        .should(never())
                        .getRequestDispatcher("/api/error"),
                () -> then(requestDispatcher)
                        .should(never())
                        .forward(any(HttpServletRequest.class), any(HttpServletResponse.class)));
    }
}
