package upbrella.be.config.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import upbrella.be.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class OAuthLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            generateErrorResponse(response);
            return false;
        }

        Long userId = (Long) session.getAttribute("userId");

        // 카카오 인증이 안되어 있는 경우, 세션이 없는 경우
        if (userId == null) {
            generateErrorResponse(response);
            return false;
        }

        if (!userRepository.existsById(userId)) {
            generateErrorResponse(response);
            return false;
        }

        return true;
    }

    private static void generateErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        // Set the content type of the response to application/json
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // Create a JSON response object with the specified format
        String jsonResponse = "{\"status\":\"fail\",\"code\":400,\"message\":\"잘못된 요청입니다.\",\"data\":null}";
        // Write the JSON response to the response stream
        try (PrintWriter writer = response.getWriter()) {
            writer.write(jsonResponse);
            writer.flush();
        }
    }
}

