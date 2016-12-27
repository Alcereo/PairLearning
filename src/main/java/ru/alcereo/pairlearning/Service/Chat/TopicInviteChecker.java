package ru.alcereo.pairlearning.Service.Chat;


import ru.alcereo.pairlearning.Service.TopicService;
import ru.alcereo.pairlearning.Service.UserFront;

import java.util.List;

public class TopicInviteChecker implements InviteChecker{

    @Override
    public boolean usersInvitable(List<UserFront> users) {

        return TopicService.usersHaveIntersectionTopics(users);
    }
}
