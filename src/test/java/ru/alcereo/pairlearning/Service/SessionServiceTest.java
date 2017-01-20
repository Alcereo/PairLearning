package ru.alcereo.pairlearning.Service;

import org.junit.Test;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.models.AuthorizationData;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class SessionServiceTest {

    @Test
    public void userAuthorization() throws Exception {

        UsersDAO users = mock(UsersDAO.class);

        SessionService sessionService = new SessionService();
        sessionService.setUsers(users);

        User user = mock(User.class);
        when(user.isActive()).then(invocation -> true);
        when(user.getUid()).then(invocation -> "PasswordHash");


        when(users.findByLoginOpt(any())).then(invocation -> Option.asOption(user));

        Option<User, ?> resultOpt = sessionService
                .userAuthorization(
                        new AuthorizationData(
                                "Login",
                                "PasswordHash",
                                "SessionId")
                ).throwException();

        assertTrue(
                "Авторизация не прошла!",
                !resultOpt.isNone());

//        assertFalse(
//                "Прошла авторизация с некорректными даными",
//                sessionService.userAuthorization(null, "PasswordHash", "SessionId")
//        );
//
//        assertFalse(
//                "Прошла авторизация с некорректными даными",
//                sessionService.userAuthorization("Login", null, "SessionId")
//        );
//
//        assertFalse(
//                "Прошла авторизация с некорректными даными",
//                sessionService.userAuthorization("Login", "PasswordHash", null)
//        );

    }

}