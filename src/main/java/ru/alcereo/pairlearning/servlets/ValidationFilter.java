package ru.alcereo.pairlearning.servlets;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.SessionService;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;
import ru.alcereo.pairlearning.Service.exeptions.ValidateException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ValidationFilter implements Filter{

    private static final Logger log = LoggerFactory.getLogger(ValidationFilter.class);
    private SessionService sessionService;

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        String sessionId = req.getSession().getId();
        String uri = req.getRequestURI();
        log.debug("Фильтрую запрос сеанса: {}", sessionId);

        try {
            if (sessionService.validateSession(sessionId)) {

                sessionService
                        .getCurrentUserOpt(sessionId)
                        .map(
                                (UserFront userFront) ->{
                                    if (userFront.isActive())
                                        req.setAttribute("user", userFront);
                                    return null;
                                }
                        );

                chain.doFilter(request, response);

            }else if (
                    uri.equals("/") |
                            uri.matches("/registration.*") |
                            uri.matches("/users.*") |
                            uri.matches("/js/.+") |
                            uri.equals("/error")
                    ) {

                chain.doFilter(request, response);

            }else {

                request.getRequestDispatcher("/authorization").forward(request, response);
            }
        } catch (ValidateException | SessionServiceException e) {

            req.setAttribute("errorDescription", e.getLocalizedMessage());
            request.getRequestDispatcher("/error").forward(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
