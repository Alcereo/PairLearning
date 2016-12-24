package ru.alcereo.pairlearning.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.SessionProvider;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class UsersAPI extends HttpServlet{

    private static final Logger log = LoggerFactory.getLogger(UsersAPI.class);

    {
        log.debug("New instance of UserAPI: {}",this);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.debug("Запрос с параметрами: {}", req.getParameterMap());

        HttpSession session = req.getSession();

        if (SessionProvider.userAuthorization(
                req.getParameter("login"),
                req.getParameter("passwordHash"),
                session.getId())) {

            log.debug("Пользователь авторизовался: {}, {}", req.getParameter("login"), session.getId());

            resp.setStatus(200);

        } else {
            resp.setStatus(401);
        }

    }


}
