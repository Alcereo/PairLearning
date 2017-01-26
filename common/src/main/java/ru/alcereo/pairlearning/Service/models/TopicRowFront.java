package ru.alcereo.pairlearning.Service.models;


public interface TopicRowFront {

    UserFront getUser();

    TopicFront getTopic();

    boolean isLearn();

    boolean isTeach();

}
