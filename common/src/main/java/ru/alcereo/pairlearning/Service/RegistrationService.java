package ru.alcereo.pairlearning.Service;

import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.Service.exeptions.RegistrationException;
import ru.alcereo.pairlearning.Service.models.RegistrationData;

/**
 * Created by alcereo on 26.01.17.
 */
public interface RegistrationService {
    Option<RegResult, RegistrationException> registration(RegistrationData regDataNullable);

    /**
     * Перечисление отображающее результат
     * регистрации пользователя
     */
    public enum RegResult {
        SUCCESS,
        LOGIN_IN_USE,
        EMAIL_INCORRECT,
        ERROR
    }
}
