package ru.alcereo.pairlearning.DAO.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

    @Id
    private UUID uid;

    @Column
    private String login;

    @Column
    private String passwordHash;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private boolean active;

    @OneToMany(
            targetEntity = TopicRowEntity.class,
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<TopicRowEntity> topicRows;

    public List<TopicRowEntity> getTopicRows() {
        return topicRows;
    }

    public void setTopicRows(List<TopicRowEntity> topicRows) {
        this.topicRows = topicRows;
    }

    public UserEntity() {
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "uid=" + uid +
                ", login='" + login + '\'' +
                '}';
    }
}
