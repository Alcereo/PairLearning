package ru.alcereo.pairlearning.Service;

import org.junit.Test;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.Service.exeptions.ValidateException;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


public class SessionServiceTest {

    @Test
    public void userAuthorization() throws Exception {

        UsersDAO users = mock(UsersDAO.class);
        SessionDAO sessionDAO = mock(SessionDAO.class);

        SessionService sessionService = new SessionService();
        SessionService.setUsers(users);
        SessionService.setSessions(sessionDAO);

        User user = mock(User.class);
        when(user.isActive()).then(invocation -> true);
        when(user.getPasswordHash()).then(invocation -> "PasswordHash");


        when(users.findByLogin(any())).then(invocation -> user);

        assertTrue(
                "Авторизация не прошла!",
                sessionService.userAuthorization("Login", "PasswordHash", "SessionId"));

        assertFalse(
                "Прошла авторизация с некорректными даными",
                sessionService.userAuthorization(null, "PasswordHash", "SessionId")
        );

        assertFalse(
                "Прошла авторизация с некорректными даными",
                sessionService.userAuthorization("Login", null, "SessionId")
        );

        assertFalse(
                "Прошла авторизация с некорректными даными",
                sessionService.userAuthorization("Login", "PasswordHash", null)
        );

    }

    @Test
    public void validateSession() throws Exception, ValidateException {

        UsersDAO users = mock(UsersDAO.class);
        SessionDAO sessionDAO = mock(SessionDAO.class);

        SessionService sessionService = new SessionService();
        SessionService.setUsers(users);
        SessionService.setSessions(sessionDAO);

        when(sessionDAO.getSessionById(any())).then(
                invocation -> new Session(null, null));

        assertTrue(
                "Не прошла валидация пользователя",
                SessionService.validateSession("SessionId")
        );

//        assertFalse(
//                "Прошла валидация с некорректными данными",
//                sessionService.validateSession(null)
//        );

    }

    @Test
    public void getCurrentUser() throws Exception {

        UsersDAO users = mock(UsersDAO.class);
        SessionDAO sessionDAO = mock(SessionDAO.class);

        User user = mock(User.class);
        Session session = mock(Session.class);

        when(session.getUser()).then(invocation -> user);

        when(sessionDAO.getSessionById(any())).then(invocation -> session);

        SessionService sessionService = new SessionService();
        SessionService.setUsers(users);
        SessionService.setSessions(sessionDAO);

        assertEquals(
                "Вернул не того пользователя",
                sessionService.getCurrentUser("SessionId"),
                user
                );

        assertEquals(
                "Вернул не нулевой возврат",
                sessionService.getCurrentUser(null),
                null
        );

    }

}