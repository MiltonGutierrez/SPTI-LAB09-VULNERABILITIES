package eci.cvds.armario.configuration;

import eci.cvds.armario.model.Roles;
import eci.cvds.armario.model.Session;
import eci.cvds.armario.repository.SessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final SessionRepository sessionRepository;

    @Autowired
    public AuthInterceptor(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("BasicAuthInterceptor::preHandle()");
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }
        String path = request.getRequestURI();
        String authToken = request.getHeader("authToken");
        if (authToken != null) {
            Session session = sessionRepository.findByToken(UUID.fromString(authToken));
            if (session != null) {
                Duration duration = Duration.between(Instant.now(), session.getTimestamp());
                long oneHour = 60L * 60L;
                if (duration.getSeconds() > oneHour) {
                    sessionRepository.delete(session);
                    response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "SessionTimeout");
                    return false;
                } else if (((path.startsWith("/user/admin") || path.startsWith("/login/eliminarSesiones")) && session.getUser().getRole().equals(Roles.ADMINISTRADOR)) || (path.startsWith("/user/client") && session.getUser().getRole().equals(Roles.CLIENTE))) {
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    return true;
                } else {
                    response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Route does not found");
                    return false;
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session does not exist in database");
                return false;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token does not exist in header authToken");
            return false;
        }
    }
}