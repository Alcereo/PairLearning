package ru.alcereo.pairlearning.Service.models;

//import ru.alcereo.pairlearning.Service.EntityMapper;

import java.io.Serializable;

public class TopicRow implements TopicRowFront, Serializable{

    private static final long serialVersionUID = 1L;

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
