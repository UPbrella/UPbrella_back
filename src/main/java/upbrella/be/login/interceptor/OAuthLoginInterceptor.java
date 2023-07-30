package upbrella.be.login.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import upbrella.be.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class OAuthLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();

        Long userId = (Long)session.getAttribute("userId");

        // 카카오 인증이 안되어있는 경우
        if (userId == null) {
            return false;
        }

        // 인증된 경우
        if (userRepository.existsByIdAndNameIsNotNullAndPhoneNumberIsNotNull(userId)) {
            return true;
        }
        // 카카오 인증이 되었지만, 회원 가입을 하지 않았을 경우
        return false;
    }
}

