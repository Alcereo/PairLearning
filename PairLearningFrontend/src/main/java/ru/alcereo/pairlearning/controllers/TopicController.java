package ru.alcereo.pairlearning.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alcereo.exoption.Option;
//import ru.alcereo.pairlearning.Service.TopicService.TopicLearnPredicateChanger;
//import ru.alcereo.pairlearning.Service.TopicService.TopicRowChanger;
//import ru.alcereo.pairlearning.Service.TopicService.TopicRowChanger;
import ru.alcereo.pairlearning.Service.TopicService.TopicChangeData;
import ru.alcereo.pairlearning.Service.TopicService.TopicPredicate;
import ru.alcereo.pairlearning.Service.TopicService.TopicService;
//import ru.alcereo.pairlearning.Service.TopicService.TopicTeachPredicateChanger;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
public class TopicController {
    private static final Logger log = LoggerFactory.getLogger(TopicController.class);

    private final HttpServletRequest request;

    private TopicService topicService;

    @Autowired
    public void setTopicService(TopicService topicService) {
        this.topicService = topicService;
    }

    @Autowired
    public TopicController(HttpServletRequest request) {
        this.request = request;
    }

    @RequestMapping(value = "/topic/api/concrete", method = RequestMethod.POST)
    public ResponseEntity setTopicPredicate(
            @RequestParam(value = "value") String value,
            @RequestParam(value = "predicate") boolean predicate,
            @RequestParam(value = "id") long id,
            Principal principal
    ){

        UserFront user = (UserFront)((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        ResponseEntity result;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-16");

        TopicChangeData data = Arrays.stream(TopicPredicate.values())
                .filter(topicPredicate -> topicPredicate.equalToName(value))
                .findFirst()
                .map(topicPredicate ->
                        new TopicChangeData(
                                predicate,
                                id,
                                topicPredicate,
                                user
                        )
                ).orElse(null);

        if (data==null){
            log.error("Неизвестный предикат: {}", value);
            result = new ResponseEntity<>("Ошибка запроса. Предикат: "+value+" не наиден.",
                        requestHeaders,
                        HttpStatus.BAD_REQUEST);
        }

        Option<Boolean,?> setResult = topicService.setTopicRow(data);

        if (!setResult.isException()) {
            result = new ResponseEntity(HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Ошибка сервиса тем."+setResult.getExceptionMessage(),
                    requestHeaders,
                    HttpStatus.BAD_REQUEST);
        }

        return result;
    }

}
