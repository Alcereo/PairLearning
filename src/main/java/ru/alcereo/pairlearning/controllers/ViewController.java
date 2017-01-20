package ru.alcereo.pairlearning.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.Service.TopicService.TopicService;
import ru.alcereo.pairlearning.Service.models.TopicRowFront;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
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
    public String userCabinet(Model model, HttpServletRequest request, Principal principal){
        String result;

        UserFront user = (UserFront)((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        Option<List<TopicRowFront>,?> optResult = topicService.getUserTopicOpt(user);

        if (!optResult.isException()){
            model.addAttribute("user", user);
            model.addAttribute("topicRows", optResult.getOrElse(null));
            result = "userCabinet";
        }else {
            log.debug(optResult.getExceptionMessage());
            request.setAttribute("errorDescription", optResult.getExceptionMessage());
            result = "errorPage";
        }

        return result;
    }

    @RequestMapping("/chatroom")
    public String chatRoom(Model model, Principal principal){

        UserFront user = (UserFront)((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        model.addAttribute("user", user);

        return "ChatRoom";
    }

//    ------------------

    @RequestMapping("/error")
    public String errorPage(){
        return "errorPage";
    }

}
