package ru.alcereo.pairlearning.DAO.models;

import ru.alcereo.pairlearning.Service.models.TopicFront;

import java.util.UUID;

/**
 * Created by alcereo on 27.12.16.
 */
public class Topic implements TopicFront{

    private UUID uid;
    private long id;
    private String title;

    public Topic(UUID uid, long id, String title) {
        this.uid = uid;
        this.id = id;
        this.title = title;
    }

    public UUID getUid() {
        return uid;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topic topic = (Topic) o;

        if (id != topic.id) return false;
        if (uid != null ? !uid.equals(topic.uid) : topic.uid != null) return false;
        return title != null ? title.equals(topic.title) : topic.title == null;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
