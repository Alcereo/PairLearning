package ru.alcereo.pairlearning.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.Service.RegistrationService;
import ru.alcereo.pairlearning.Service.models.ConfirmationData;
import ru.alcereo.pairlearning.Service.models.RegistrationData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
public class RegistrationRestController {

    private final HttpServletRequest request;

    private RegistrationService registrationService;

    @Autowired
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

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

        Option<RegistrationService.RegResult,?> regResultOpt = registrationService.registration(
                new RegistrationData(
                        session.getId(),
                        login,
                        name,
                        passwordHash,
                        email
                ));

        if (!regResultOpt.isException()) {

                switch (regResultOpt.getOrElse(RegistrationService.RegResult.ERROR)) {
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
        }else{
            result = new ResponseEntity<>(
                    "Ошибка регистрации: " + regResultOpt.getExceptionMessage(),
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

        Option<Boolean,?> confirmRes = registrationService
                .confirmRegistration(
                        new ConfirmationData(
                                session.getId(),
                                code
                        ));

        if (!confirmRes.isException()){
            if (confirmRes.getOrElse(false))

                result = new ResponseEntity(HttpStatus.OK);

            else
                result = new ResponseEntity<>(
                        "Код подтверждения не корректен",
                        requestHeaders,
                        HttpStatus.BAD_REQUEST);

        }else {
            result = new ResponseEntity<>(
                    "Ошибка сервиса регистрации. "+confirmRes.getExceptionMessage(),
                    requestHeaders,
                    HttpStatus.BAD_REQUEST);

        }

        return result;
    }

}
