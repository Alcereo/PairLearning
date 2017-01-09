package ru.alcereo.pairlearning.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.Service.SessionService;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class AuthorizationRestController {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationRestController.class);

    private final HttpServletRequest request;

    private SessionService sessionService;

    @Autowired
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    @Autowired
    public AuthorizationRestController(HttpServletRequest request) {
        this.request = request;
    }

    @RequestMapping(value = "/users/api/auth", method = RequestMethod.POST)
    public ResponseEntity authorization(
            @RequestParam(value = "login") String login,
            @RequestParam(value = "passwordHash") String password

    ){

        HttpSession session = request.getSession();
        ResponseEntity result;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-16");

        Option<Boolean,?> authResultOpt = sessionService.userAuthorization(
                login,
                password,
                session.getId()
        );

        if (!authResultOpt.isException()){
            if (authResultOpt.getOrElse(false)) {

                log.debug("Пользователь авторизовался: {}, {}", login, session.getId());

                result = new ResponseEntity(HttpStatus.OK);

            } else {

                result = new ResponseEntity<>("Некорректное имя пользователя или пароль",
                        requestHeaders,
                        HttpStatus.UNAUTHORIZED);

            }
        } else {

            log.warn(authResultOpt.getExceptionMessage());

            result = new ResponseEntity<>("Ошибка сервиса авторизации. "+authResultOpt.getExceptionMessage(),
                    requestHeaders,
                    HttpStatus.UNAUTHORIZED);

        }

        return result;
    }

    @RequestMapping(value = "/users/api/logout", method = RequestMethod.POST)
    public ResponseEntity logOut(HttpServletResponse resp){

        HttpSession session = request.getSession();
        ResponseEntity result;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-16");

        try {
            sessionService.deleteSession(session.getId());
            result = new ResponseEntity(HttpStatus.OK);

        } catch (SessionServiceException e) {
            result = new ResponseEntity<>("Ошибка сервиса сессий. "+e.getLocalizedMessage(),
                    requestHeaders,
                    HttpStatus.UNAUTHORIZED);

        }

        return result;
    }

}
