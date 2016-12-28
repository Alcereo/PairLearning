package ru.alcereo.pairlearning.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.RegistrationService;
import ru.alcereo.pairlearning.Service.exeptions.RegistrationException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/registration/api")
public class RegistrationServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(RegistrationServlet.class);

    private static final RegistrationService registrationService = new RegistrationService();

    {
        log.debug("New instance: {}",this);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){

        log.debug("Запрос с параметрами: {}", req.getParameterMap());

        String action = req.getParameter("action");

        switch (action) {
            case "registration":
                registration(req, resp);
                break;
            case "confiramtion":
                confirmation(req, resp);
                break;
            default:
                try {
                    resp.getWriter().write(
                            "Действие action не распознано"
                    );
                    resp.setStatus(400);
                } catch (IOException e1) {
                    log.warn(e1.getLocalizedMessage());
                    resp.setStatus(400);
                }
        }

    }

    private void registration(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();

        try {
            switch (
                    registrationService.registration(
                            session.getId(),
                            req.getParameter("login"),
                            req.getParameter("name"),
                            req.getParameter("passwordHash"),
                            req.getParameter("email")
                    )) {
                case SUCCESS:
                    resp.setStatus(200);
                    break;
                case LOGIN_IN_USE:
                    resp.getWriter().write(
                            "Логин уже используется"
                    );
                    resp.setStatus(409);
                    break;
                case EMAIL_INCORRECT:
                    resp.getWriter().write(
                            "Почтовый адрес не корректен"
                    );
                    resp.setStatus(400);
                    break;

                // Теоретически недостежимо
                default:
                    resp.setStatus(400);
            }
        } catch (RegistrationException e) {
            log.warn(e.getLocalizedMessage());
            try {
                resp.getWriter().write(
                        "Ошибка регистрации: "+e.getLocalizedMessage()
                );
                resp.setStatus(400);
            } catch (IOException e1) {
                log.warn(e.getLocalizedMessage());
                resp.setStatus(400);
            }
        } catch (IOException e) {
            log.warn(e.getLocalizedMessage());
            resp.setStatus(400);
        }

    }

    private void confirmation(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();

        Integer code = 0;

        try {
            code = new Integer(req.getParameter("code"));
        }catch (NumberFormatException e){
            try {
                resp.getWriter().write(
                        "Ошибка обработки кода"
                );
                resp.setStatus(400);
            } catch (IOException e1) {
                log.warn(e.getLocalizedMessage());
                resp.setStatus(400);
            }
        }

        if (code == 0) {

            log.debug("Получили код подтверждения: {}", code);

            try {
                if (registrationService.confirmRegistration(
                        session.getId(),
                        code))
                    resp.setStatus(200);
                else
                    try {
                        resp.getWriter().write(
                                "Код подтверждения не корректен"
                        );
                        resp.setStatus(400);
                    } catch (IOException e) {
                        log.warn(e.getLocalizedMessage());
                        resp.setStatus(400);
                    }
            } catch (RegistrationException e) {
                try {
                    resp.getWriter().write(
                            "Ошибка сервиса регистрации. "+e.getLocalizedMessage()
                    );
                    resp.setStatus(400);
                } catch (IOException e1) {
                    log.warn(e1.getLocalizedMessage());
                    resp.setStatus(400);
                }
            }
        }
    }

}
