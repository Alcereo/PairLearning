package ru.alcereo.pairlearning.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.alcereo.pairlearning.Service.TopicService;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ViewController {

    private static final Logger log = LoggerFactory.getLogger(ViewController.class);

    private TopicService topicService;

    @Autowired
    public void setTopicService(TopicService topicService) {
        this.topicService = topicService;
    }

    @RequestMapping("/")
    public String index(){
        return "index";
    }

//   ----------------

    @RequestMapping("/authorization")
    public String authorizationError(){
        return "authorizationError";
    }

    @RequestMapping("/registration")
    public String registration(){
        return "registration";
    }

//    -----------------

    @RequestMapping("/usercabinet")
    public String userCabinet(Model model, HttpServletRequest request){
        String result;

        try {
            model.addAttribute("topicRows",topicService.getUserTopic((UserFront)request.getAttribute("user")));
            result = "userCabinet";

        } catch (TopicServiceException e) {
            log.debug(e.getLocalizedMessage());
            request.setAttribute("errorDescription", e.getLocalizedMessage());
            result = "errorPage";

        }


        return result;
    }

    @RequestMapping("/chatroom")
    public String chatRoom(){
        return "ChatRoom";
    }

//    ------------------

    @RequestMapping("/error")
    public String errorPage(){
        return "errorPage";
    }

}
