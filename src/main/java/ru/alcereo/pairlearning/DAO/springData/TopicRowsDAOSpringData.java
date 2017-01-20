package ru.alcereo.pairlearning.DAO.springData;

import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.Entities.TopicEntity;
import ru.alcereo.pairlearning.DAO.Entities.TopicRowEntity;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by alcereo on 20.01.17.
 */
public class TopicRowsDAOSpringData implements TopicRowsDAO{

    public void setRepository(TopicRowViewEntityRepository repository) {
        this.repository = repository;
    }

    private TopicRowViewEntityRepository repository;

    @Override
    public Option<Boolean, TopicRowDataError> setLearnPredicate(Long id, User user, boolean predicate) {
        return Option.asOption(
                () -> repository.setLearnPredicateByUserUID(predicate, user.getUid(), id)>0
        );
    }

    @Override
    public Option<Boolean, TopicRowDataError> setTeachPredicate(Long id, User user, boolean predicate) {
        return Option.asOption(
                () -> repository.setTeachPredicateByUserUID(predicate, user.getUid(), id)>0
        );
    }

    @Override
    public Option<List<TopicRowEntity>, TopicRowDataError> getAllByUserUIDOpt(UUID uuid) {
        return Option.asOption(() -> repository.findByUserUid(uuid));
    }

    @Override
    public Option<Set<TopicEntity>, TopicRowDataError> getLearnTopicsByUserUID(UUID uuid) {
        return Option.asOption(
                () -> repository
                        .findByUserUidAndLearnTrue(uuid)
                        .stream()
                        .map(TopicRowEntity::getTopic)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public Option<Set<TopicEntity>, TopicRowDataError> getTeachTopicsByUserUID(UUID uuid) {
        return Option.asOption(
                () -> repository
                        .findByUserUidAndTeachTrue(uuid)
                        .stream()
                        .map(TopicRowEntity::getTopic)
                        .collect(Collectors.toSet())
        );
    }
}
