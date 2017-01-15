package ru.alcereo.pairlearning.Service;

import org.junit.Test;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.Service.models.RegistrationData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RegistrationServiceTest {

    @Test
    public void registration() throws Exception {

        UsersDAO users = mock(UsersDAO.class);
        SessionDAO sessionDAO = mock(SessionDAO.class);
        SendingService sendingService = mock(SendingService.class);

        RegistrationService registrationService = new RegistrationService();
        registrationService.setSessions(sessionDAO);
        registrationService.setUsers(users);
        registrationService.setSendingService(sendingService);

        RegistrationService.RegResult result = registrationService.registration(
                new RegistrationData(
                        "SessionId",
                        "login",
                        "name",
                        "passHash",
                        "mail"
                )).getOrElse(null);

        assertEquals(
                "Регистрация не прошла",
                result,
                RegistrationService.RegResult.SUCCESS);


        assertEquals(
                "Регистрация прошла с некорректными даными",
                RegistrationService.RegResult.ERROR,
                registrationService.registration(
                       new RegistrationData(
                               null,
                               "login",
                               "name",
                               "passHash",
                               "mail"
                       )));

        assertEquals(
                "Регистрация прошла с некорректными даными",
                RegistrationService.RegResult.ERROR,
                registrationService.registration(
                        new RegistrationData(
                                "SessionId",
                                null,
                                "name",
                                "passHash",
                                "mail"
                        )));

        assertEquals(
                "Регистрация прошла с некорректными даными",
                RegistrationService.RegResult.ERROR,
                registrationService.registration(
                        new RegistrationData(
                                "SessionId",
                                "login",
                                "name",
                                null,
                                "mail"
                        )));


    }

}