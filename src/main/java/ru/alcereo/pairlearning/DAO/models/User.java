package ru.alcereo.pairlearning.DAO.models;

import ru.alcereo.pairlearning.Service.models.UserFront;

import java.util.UUID;

public class User implements UserFront {

    private final UUID uid;
    private final String login;
    private final String passwordHash;
    private final String name;
    private final String email;
    private final boolean active;

    public User(UUID uid, String login, String passwordHash, String name, String email, boolean active) {
        this.uid = uid;
        this.login = login;
        this.passwordHash = passwordHash;
        this.name = name;
        this.email = email;
        this.active = active;
    }

    public User() {
        this(
                UUID.fromString(""),
                "",
                "",
                "",
                "",
                false
        );
    }

    public UUID getUid() {
        return uid;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public User makeActive(){
        return new User(
                this.getUid(),
                this.login,
                this.passwordHash,
                this.getName(),
                this.email,
                true
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (uid != null ? !uid.equals(user.uid) : user.uid != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        return passwordHash != null ? passwordHash.equals(user.passwordHash) : user.passwordHash == null;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (passwordHash != null ? passwordHash.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", login='" + login + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }

}
