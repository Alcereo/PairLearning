package ru.alcereo.pairlearning.Service;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.alcereo.pairlearning.DAO.SessionDAO;
import ru.alcereo.pairlearning.DAO.UsersDAO;
import ru.alcereo.pairlearning.DAO.models.Session;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.models.RegistrationData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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

    @Test
    public void confirmRegistration() throws Exception {

        UsersDAO        users           = mock(UsersDAO.class);
        SessionDAO      sessionDAO      = mock(SessionDAO.class);
        SendingService  sendingService  = mock(SendingService.class);

        RegistrationService registrationService = new RegistrationService();
        registrationService.setSessions(sessionDAO);
        registrationService.setUsers(users);
        registrationService.setSendingService(sendingService);

        class MyVoidAnswerWithString implements Answer<Void>{

            public String mockMessage;

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                mockMessage = invocation.getArgument(0);
                return null;
            }
        }

        class MyVoidAnswerWithUser implements Answer<Void>{
            public User user;

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                user = invocation.getArgument(0);
                return null;
            }
        }

        MyVoidAnswerWithString codeSendingAnswer = new MyVoidAnswerWithString();
        MyVoidAnswerWithUser myVoidAnswerWithUser = new MyVoidAnswerWithUser();

        doAnswer(codeSendingAnswer).when(sendingService).send(anyString(), eq("mail"));
        doAnswer(myVoidAnswerWithUser).when(users).addUser(any());

        RegistrationService.RegResult result = registrationService.registration(
                new RegistrationData(
                        "SessionId",
                        "login",
                        "name",
                        "passHash",
                        "mail"
                )).getOrElse(null);

        Matcher matcher = Pattern.compile("\\d\\d\\d\\d").matcher(codeSendingAnswer.mockMessage);
        matcher.find();
        int code = new Integer(matcher.group());

        when(sessionDAO.getSessionById(anyString())).then(
                invocation -> new Session("SessionId",
                        myVoidAnswerWithUser.user)
        );
        when(users.makeActive(any())).then(invocation -> myVoidAnswerWithUser.user);

        assertTrue(
                "Не выполнилось подстверждение после регистрации",
                registrationService.confirmRegistration("SessionId",code)
        );

//        assertFalse(
//                "Прошла регистрация с некорректными данными",
//                registrationService.confirmRegistration(null,code)
//        );

    }

}