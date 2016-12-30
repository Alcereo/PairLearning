package ru.alcereo.pairlearning.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

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
    public String userCabinet(){
        return "userCabinet";
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
