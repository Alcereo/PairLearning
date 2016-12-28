package ru.alcereo.pairlearning.DAO;

import ru.alcereo.pairlearning.DAO.models.Topic;
import ru.alcereo.pairlearning.DAO.models.User;
import ru.alcereo.pairlearning.Service.models.TopicRowFront;


public class TopicRow implements TopicRowFront {

    private boolean learn;
    private boolean teach;
    private User user;
    private Topic topic;

    public TopicRow(boolean learn, boolean teach, User user, Topic topic) {
        this.learn = learn;
        this.teach = teach;
        this.user = user;
        this.topic = topic;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    @Override
    public boolean isLearn() {
        return learn;
    }

    @Override
    public boolean isTeach() {
        return teach;
    }

}
