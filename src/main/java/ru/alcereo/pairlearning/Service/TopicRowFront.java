package ru.alcereo.pairlearning.Service;


public interface TopicRowFront {

    UserFront getUser();

    TopicFront getTopic();

    boolean isLearn();

    boolean isTeach();

}
