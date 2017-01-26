package ru.alcereo.pairlearning.DAO.Entities;


import java.io.Serializable;

public class TopicRowsIdClass implements Serializable{

    private UserEntity user;

    private TopicEntity topic;

    public TopicRowsIdClass(UserEntity user, TopicEntity topic) {
        this.user = user;
        this.topic = topic;
    }

    public TopicRowsIdClass() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicRowsIdClass that = (TopicRowsIdClass) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return topic != null ? topic.equals(that.topic) : that.topic == null;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        return result;
    }

}
