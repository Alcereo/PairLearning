package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.fUtils.Option;
import ru.alcereo.pairlearning.Service.Chat.exceptions.ChatInviteException;
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
    Option<Boolean, ChatInviteException> usersInvitable(List<UserFront> users);
}
