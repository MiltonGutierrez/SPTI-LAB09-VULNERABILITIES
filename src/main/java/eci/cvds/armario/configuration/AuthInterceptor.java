package eci.cvds.armario.configuration;

import eci.cvds.armario.model.Session;
import eci.cvds.armario.model.Roles;
import eci.cvds.armario.repository.SessionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final SessionRepository sessionRepository;
    @Autowired
    public AuthInterceptor(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    private String getCookieValue(HttpServletRequest req, String cookieName) {
        if (req.getCookies() == null) {
            return null;
        }return Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String authToken = getCookieValue(request, "authToken");
        if (authToken != null) {
            Session session = sessionRepository.findByToken(UUID.fromString(authToken));
            if (session != null) {
                Duration duration = Duration.between(Instant.now(), session.getTimestamp());
                long oneHour = 60L * 60L;
                if(duration.getSeconds() > oneHour) {
                    sessionRepository.delete(session);
                    response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "SessionTimeout");
                    return false;
                }else if(path.startsWith("/user/userId")){
                    response.setHeader("Access-Control-Allow-Credentials","true");
                    return true;
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session does not exist in database");
                return false;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token does not exist in cookies");
            return false;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}