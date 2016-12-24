package ru.alcereo.pairlearning.DAO;


import ru.alcereo.pairlearning.Service.UserFront;

import java.util.UUID;

public class User implements UserFront {

    private final UUID uid;
    private final String login;
    private final String passwordHash;
    private final String name;

    public User(UUID id, String login, String passwordHash, String name) {
        this.uid = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.name = name;
    }

    public User() {
        this(
                UUID.fromString(""),
                "",
                "",
                ""
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


}
