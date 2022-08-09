package com.gaofeicm.qqbot.controller;

import com.gaofeicm.qqbot.service.BotServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Gaofeicm
 */
@RestController
public class BotController {

    @Resource
    private BotServiceImpl robotService;

    @PostMapping("/send")
    public void QqRobotEven(HttpServletRequest request){
        robotService.QqRobotEvenHandle(request);
    }
}
