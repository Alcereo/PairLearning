package ru.alcereo.pairlearning.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.SessionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/users/api")
public class AuthorisationServlet extends HttpServlet{

    private final SessionService sessionService = new SessionService();

    private static final Logger log = LoggerFactory.getLogger(AuthorisationServlet.class);

    {
        log.debug("New instance: {}",this);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.debug("Запрос с параметрами: {}", req.getParameterMap());

        HttpSession session = req.getSession();

        String action = req.getParameter("action");

        if (action!=null){

            switch (action){
                case "auth":

                    if (SessionService.userAuthorization(
                            req.getParameter("login"),
                            req.getParameter("passwordHash"),
                            session.getId())) {

                        log.debug("Пользователь авторизовался: {}, {}", req.getParameter("login"), session.getId());

                        resp.setStatus(200);

                    } else {
                        resp.setStatus(401);
                    }

                    break;

                case "exit":

                    SessionService.deleteSession(session.getId());
                    resp.sendRedirect("/");

                    break;

                default:
                    resp.setStatus(401);
            }

        }else
            resp.setStatus(401);

    }

}
