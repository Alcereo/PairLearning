package ru.alcereo.pairlearning.DAO.springData;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.alcereo.pairlearning.DAO.Entities.TopicRowEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TopicRowViewEntityRepository extends CrudRepository<TopicRowEntity,UUID> {

    @Modifying
    @Transactional
    @Query("update TopicRowEntity set learn=:predicate where " +
            "user in (from UserEntity as u where uid=:uuid) " +
            "and " +
            "topic in (from TopicEntity as t where id=:id)")
    int setLearnPredicateByUserUID(@Param("predicate")  Boolean predicate,
                                   @Param("uuid")       UUID    uuid,
                                   @Param("id")         Long    id);

    @Modifying
    @Transactional
    @Query("update TopicRowEntity set teach=:predicate where " +
            "user in (from UserEntity as u where uid=:uuid) " +
            "and " +
            "topic in (from TopicEntity as t where id=:id)")
    int setTeachPredicateByUserUID(@Param("predicate")  Boolean predicate,
                                   @Param("uuid")       UUID    uuid,
                                   @Param("id")         Long    id);

    @Query(value = "SELECT * FROM topic_rows_view WHERE user_uid = ?1", nativeQuery = true)
    List<TopicRowEntity> findByUserUid(UUID user_uid);

    Set<TopicRowEntity> findByUserUidAndLearnTrue(UUID uuid);

    Set<TopicRowEntity> findByUserUidAndTeachTrue(UUID uuid);

}
