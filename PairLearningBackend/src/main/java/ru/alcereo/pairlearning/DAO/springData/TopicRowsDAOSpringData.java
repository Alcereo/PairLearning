package ru.alcereo.pairlearning.DAO.springData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.alcereo.exoption.Func;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.Entities.TopicEntity;
import ru.alcereo.pairlearning.DAO.Entities.TopicRowEntity;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.DAO.exceptions.TopicRowDataError;
import ru.alcereo.pairlearning.Service.models.UserFront;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Repository
public class TopicRowsDAOSpringData implements TopicRowsDAO{
    private static final Logger log = LoggerFactory.getLogger(TopicRowsDAOSpringData.class);

    private TopicRowViewEntityRepository repository;

    @Autowired
    public void setRepository(TopicRowViewEntityRepository repository) {
        this.repository = repository;
    }


//     * -------------------------------------------------------- *
//     *                     ОСНОВНЫЕ ФУНКЦИИ                     *
//     * -------------------------------------------------------- *

    @Override
    public Option<Boolean, TopicRowDataError> setLearnPredicate(Long id, UserFront user, boolean predicate) {
        return optionedRepositoryAction(repository ->
                repository.setLearnPredicateByUserUID(predicate, user.getUid(), id)>0
        );
    }

    @Override
    public Option<Boolean, TopicRowDataError> setTeachPredicate(Long id, UserFront user, boolean predicate) {
        return optionedRepositoryAction(repository ->
                repository.setTeachPredicateByUserUID(predicate, user.getUid(), id)>0
        );
    }

    @Override
    public Option<List<TopicRowEntity>, TopicRowDataError> getAllByUserUIDOpt(UUID uuid) {
        return optionedRepositoryAction(repository ->
                repository.findByUserUid(uuid)
        );
    }

    @Override
    public Option<Set<TopicEntity>, TopicRowDataError> getLearnTopicsByUserUID(UUID uuid) {
        return optionedRepositoryAction(repository ->
                repository
                .findByUserUidAndLearnTrue(uuid)
                .stream()
                .map(TopicRowEntity::getTopic)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Option<Set<TopicEntity>, TopicRowDataError> getTeachTopicsByUserUID(UUID uuid) {
        return optionedRepositoryAction(repository ->
                repository
                .findByUserUidAndTeachTrue(uuid)
                .stream()
                .map(TopicRowEntity::getTopic)
                .collect(Collectors.toSet())
        );
    }


//     * -------------------------------------------------------- *
//     *                       СЛУЖЕБНЫЕ                          *
//     * -------------------------------------------------------- *

    private<RESULT> Option<RESULT, TopicRowDataError>
    optionedRepositoryAction(Func<TopicRowViewEntityRepository, RESULT,TopicRowDataError> func){
        return Option.asOption(
                () -> func.execute(repository)
        ).wrapException(this::errorWrapper);
    }

    TopicRowDataError errorWrapper(Throwable cause){
        log.error(cause.getLocalizedMessage());
        return new TopicRowDataError("Ошибка получения данных тем", cause);
    }
}
