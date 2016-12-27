package ru.alcereo.pairlearning.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.RegistrationService;

import javax.servlet.ServletException;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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
                resp.setStatus(400);
        }

    }

    private void registration(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();

        switch (
                registrationService.registration(
                        session.getId(),
                        req.getParameter("login"),
                        req.getParameter("name"),
                        req.getParameter("passwordHash"),
                        req.getParameter("email")
                ))
        {
            case SUCCESS:
                resp.setStatus(200);
                break;
            case LOGIN_IN_USE:
                resp.getWriter().write(
                        "LOGIN_IN_USE"
                );
                resp.setStatus(409);
                break;
            case EMAIL_INCORRECT:
                resp.getWriter().write(
                        "EMAIL_INCORRECT"
                );
                resp.setStatus(400);
                break;
            default:
                resp.setStatus(400);
        }
    }

    private void confirmation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();

        Integer code = null;

        try {
            code = new Integer(req.getParameter("code"));
        }catch (NumberFormatException e){
            resp.getWriter().write(
                    "CODE_INCORRECT"
            );
            resp.setStatus(400);
        }

        log.debug("Получили код подтверждения: {}", code);

        if(registrationService.confirmRegistration(
                session.getId(),
                code))
            resp.setStatus(200);
        else
            resp.setStatus(400);

    }

}
