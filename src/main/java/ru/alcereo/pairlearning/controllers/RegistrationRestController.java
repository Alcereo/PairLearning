package ru.alcereo.pairlearning.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alcereo.pairlearning.Service.RegistrationService;
import ru.alcereo.pairlearning.Service.exeptions.RegistrationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
public class RegistrationRestController {

    private final HttpServletRequest request;

    @Autowired
    public RegistrationRestController(HttpServletRequest request) {
        this.request = request;
    }

    @RequestMapping(value = "/registration/api/registration")
    public ResponseEntity registration(
            @RequestParam(value = "login") String login,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "passwordHash") String passwordHash,
            @RequestParam(value = "email") String email
    ){

        HttpSession session = request.getSession();
        ResponseEntity result;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-16");

        try {
            switch (RegistrationService.registration(
                            session.getId(),
                            login,
                            name,
                            passwordHash,
                            email
                    )) {
                case SUCCESS:
                    result = new ResponseEntity(HttpStatus.OK);
                    break;

                case LOGIN_IN_USE:
                    result = new ResponseEntity<>("Логин уже используется",
                            requestHeaders,
                            HttpStatus.CONFLICT);
                    break;

                case EMAIL_INCORRECT:
                    result = new ResponseEntity<>("Почтовый адрес не корректен",
                            requestHeaders,
                            HttpStatus.BAD_REQUEST);
                    break;

                // Теоретически недостежимо
                default:
                    result = new ResponseEntity<>(requestHeaders,
                            HttpStatus.BAD_REQUEST);
            }
        } catch (RegistrationException e) {
            result = new ResponseEntity<>(
                    "Ошибка регистрации: " + e.getLocalizedMessage(),
                    requestHeaders,
                    HttpStatus.BAD_REQUEST);
        }

        return result;
    }

    @RequestMapping(value = "/registration/api/confirmation")
    public ResponseEntity confirm(
            @RequestParam(value = "code") int code
    ){

        HttpSession session = request.getSession();
        ResponseEntity result;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-16");

        try {
            if (RegistrationService
                    .confirmRegistration(
                            session.getId(),
                            code))

                result = new ResponseEntity(HttpStatus.OK);

            else
                result = new ResponseEntity<>(
                        "Код подтверждения не корректен",
                        requestHeaders,
                        HttpStatus.BAD_REQUEST);

        } catch (RegistrationException e) {
            result = new ResponseEntity<>(
                    "Ошибка сервиса регистрации. "+e.getLocalizedMessage(),
                    requestHeaders,
                    HttpStatus.BAD_REQUEST);

        }

        return result;
    }

}
