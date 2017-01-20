package ru.alcereo.pairlearning.DAO.springData;

import org.springframework.data.repository.CrudRepository;
import ru.alcereo.pairlearning.DAO.Entities.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserEntityRepository extends CrudRepository<UserEntity, UUID> {
    List<UserEntity> findByLogin(String login);
}
