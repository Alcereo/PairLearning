package ru.alcereo.pairlearning.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alcereo.pairlearning.Service.TopicLearnPredicateChanger;
import ru.alcereo.pairlearning.Service.TopicRowChanger;
import ru.alcereo.pairlearning.Service.TopicService;
import ru.alcereo.pairlearning.Service.TopicTeachPredicateChanger;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.servlet.http.HttpServletRequest;


@RestController
public class TopicRestController {
    private static final Logger log = LoggerFactory.getLogger(TopicRestController.class);

    private final HttpServletRequest request;

    private TopicService topicService;

    @Autowired
    public void setTopicService(TopicService topicService) {
        this.topicService = topicService;
    }

    @Autowired
    public TopicRestController(HttpServletRequest request) {
        this.request = request;
    }


    @RequestMapping(value = "/topic/api/concrete", method = RequestMethod.POST)
    public ResponseEntity setTopicPredicate(
            @RequestParam(value = "value") String value,
            @RequestParam(value = "predicate") boolean predicate,
            @RequestParam(value = "id") long id
    ){

        ResponseEntity result;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "text/html; charset=utf-16");

        TopicRowChanger changer;

        switch (value) {
            case "learn":
                // new Factory - LEARN
                changer = new TopicLearnPredicateChanger();

                break;
            case "teach":
                //new Factory - TEACH
                changer = new TopicTeachPredicateChanger();
                break;

            default:
                return new ResponseEntity<>("Ошибка запроса. Предикат: "+value+" не наиден.",
                        requestHeaders,
                        HttpStatus.BAD_REQUEST);
        }

        changer.setPredicateValue(predicate)
                .setTopicId(id);

        try {

            topicService.setTopicRow(changer, (UserFront) request.getAttribute("user"));
            result = new ResponseEntity(HttpStatus.OK);

        } catch (TopicServiceException e) {
            return new ResponseEntity<>("Ошибка сервиса тем."+e.getLocalizedMessage(),
                    requestHeaders,
                    HttpStatus.BAD_REQUEST);
        }

        return result;
    }

}