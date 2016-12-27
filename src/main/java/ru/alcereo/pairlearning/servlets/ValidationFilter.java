package ru.alcereo.pairlearning.servlets;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.SessionService;
import ru.alcereo.pairlearning.Service.UserFront;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ValidationFilter implements Filter{

    private static final Logger log = LoggerFactory.getLogger(ValidationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        String sessionId = req.getSession().getId();
        String uri = req.getRequestURI();
        log.debug("Фильтрую запрос сеанса: {}", sessionId);

        if (SessionService.validateSession(sessionId)) {

            UserFront user = SessionService.getCurrentUser(sessionId);
            if (user != null)
                req.setAttribute("user", user);

            chain.doFilter(request, response);

        }else if (
                uri.equals("/") |
                        uri.equals("/registration") |
                        uri.equals("/users/api") |
                        uri.matches("/js/.+")
                ) {

            chain.doFilter(request, response);

        }else {

            request.getRequestDispatcher("/authorization").forward(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
