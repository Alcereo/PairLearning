package ru.alcereo.pairlearning.Service.models;


import java.util.UUID;

public interface UserFront {

    UUID getUid();

    String getLogin();

    String getName();

    String getEmail();

    boolean isActive();

}
