package ru.alcereo.pairlearning.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.SessionService;
import ru.alcereo.pairlearning.Service.exeptions.AuthorizationException;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/users/api")
public class AuthorisationServlet extends HttpServlet{

//    private final SessionService sessionService = new SessionService();

    private static final Logger log = LoggerFactory.getLogger(AuthorisationServlet.class);

    {
        log.debug("New instance: {}",this);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        log.debug("Запрос с параметрами: {}", req.getParameterMap());

        HttpSession session = req.getSession();

        String action = req.getParameter("action");

        if (action!=null){

            switch (action){
                case "auth":

                    try {
                        if (SessionService.userAuthorization(
                                req.getParameter("login"),
                                req.getParameter("passwordHash"),
                                session.getId())) {

                            log.debug("Пользователь авторизовался: {}, {}", req.getParameter("login"), session.getId());

                            resp.setStatus(200);

                        } else {
                            try {
                                resp.setCharacterEncoding("utf-16");
                                resp.getWriter().write(
                                        "Некорректное имя пользователя или пароль"
                                );
                                resp.setStatus(401);
                            } catch (IOException e) {
                                log.warn(e.getLocalizedMessage());
                                resp.setStatus(400);
                            }
                        }
                    } catch (AuthorizationException e) {
                        log.warn(e.getLocalizedMessage());
                        try {
                            resp.setCharacterEncoding("utf-16");
                            resp.getWriter().write(
                                    "Ошибка сервиса авторизации. "+e.getLocalizedMessage()
                            );
                            resp.setStatus(401);
                        } catch (IOException e1) {
                            log.warn(e1.getLocalizedMessage());
                            resp.setStatus(400);
                        }
                    }

                    break;

                case "exit":

                    try {
                        SessionService.deleteSession(session.getId());
                        resp.sendRedirect("/");
                    } catch (SessionServiceException e) {
                        try {
                            resp.setCharacterEncoding("utf-16");
                            resp.getWriter().write(
                                    "Ошибка сервиса сессий. "+e.getLocalizedMessage()
                            );
                            resp.setStatus(401);
                        } catch (IOException e1) {
                            log.warn(e1.getLocalizedMessage());
                            resp.setStatus(400);
                        }
                    } catch (IOException e) {
                        log.warn(e.getLocalizedMessage());
                        resp.setStatus(400);
                    }

                    break;

                default:
                    resp.setStatus(401);
            }

        }else
            try {
                resp.setCharacterEncoding("utf-16");
                resp.getWriter().write(
                        "Не определено действие action"
                );
                resp.setStatus(401);
            } catch (IOException e) {
                log.warn(e.getLocalizedMessage());
                resp.setStatus(400);
            }
    }

}
