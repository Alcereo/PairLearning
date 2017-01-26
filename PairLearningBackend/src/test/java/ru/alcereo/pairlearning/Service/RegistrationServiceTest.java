package ru.alcereo.pairlearning.Service;

import org.junit.Test;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.Service.models.RegistrationData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RegistrationServiceTest {

    @Test
    public void registration() throws Exception {

        UsersDAO users = mock(UsersDAO.class);
        SendingService sendingService = mock(SendingService.class);

        RegistrationServiceIml registrationService = new RegistrationServiceIml();
        registrationService.setUsers(users);

        RegistrationServiceIml.RegResult result = registrationService.registration(
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
                RegistrationServiceIml.RegResult.SUCCESS);


        assertEquals(
                "Регистрация прошла с некорректными даными",
                RegistrationServiceIml.RegResult.ERROR,
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
                RegistrationServiceIml.RegResult.ERROR,
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
                RegistrationServiceIml.RegResult.ERROR,
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