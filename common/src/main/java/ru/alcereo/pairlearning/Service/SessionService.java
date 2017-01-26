package ru.alcereo.pairlearning.Service;

import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.Service.exeptions.SessionServiceException;
import ru.alcereo.pairlearning.Service.models.AuthorizationData;
import ru.alcereo.pairlearning.Service.models.User;

public interface SessionService {
    Option<User, SessionServiceException> userAuthorization(AuthorizationData authData_n);
}
