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

import static ru.alcereo.pairlearning.servlets.ServletUtil.respError;

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
                respError(resp,"Действие action не распознано", 400);
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
                    respError(resp,"Логин уже используется", 409);
                    break;
                case EMAIL_INCORRECT:
                    respError(resp,"Почтовый адрес не корректен", 400);
                    break;

                // Теоретически недостежимо
                default:
                    resp.setStatus(400);
            }
        } catch (RegistrationException e) {
            respError(resp, "Ошибка регистрации: " + e.getLocalizedMessage(), 400);
        }

    }

    private void confirmation(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();

        Integer code = 0;

        try {
            code = new Integer(req.getParameter("code"));
        }catch (NumberFormatException e){
            respError(resp, "Ошибка обработки кода" + e.getLocalizedMessage(), 400);
        }

        if (code != 0) {

            log.debug("Получили код подтверждения: {}", code);

            try {
                if (registrationService.confirmRegistration(
                        session.getId(),
                        code))
                    resp.setStatus(200);
                else
                    respError(resp, "Код подтверждения не корректен", 400);

            } catch (RegistrationException e) {

                respError(resp, "Ошибка сервиса регистрации. "+e.getLocalizedMessage(), 400);
            }
        }
    }

}
