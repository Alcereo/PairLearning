package ru.alcereo.pairlearning.Service.TopicService;


import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.DAO.TopicRowsDAO;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.User;

public interface TopicRowChanger {

    Option<Boolean, TopicServiceException> setPredicate(TopicChangeData data);

    Boolean canSubmitThisData(TopicChangeData data);

}
