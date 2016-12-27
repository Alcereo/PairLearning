package ru.alcereo.pairlearning.Service.Chat;

import ru.alcereo.pairlearning.Service.UserFront;

import java.util.List;


public interface InviteChecker {
    boolean usersInvitable(List<UserFront> users);
}
