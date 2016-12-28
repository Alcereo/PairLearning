package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.util.List;


/**
 * Класс проверки возможности подключения к комнате
 */
public interface InviteChecker {

    /**
     * Проверка на то, то пользователи могут быть подключены в одну комнату
     * @param users
     *  Список пользователей
     * @return
     *  true - если все пользователи в списке могут подключиться к комнате
     */
    boolean usersInvitable(List<UserFront> users) throws TopicServiceException;
}
