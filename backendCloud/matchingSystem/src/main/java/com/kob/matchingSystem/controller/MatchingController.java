package com.kob.matchingSystem.controller;

import com.kob.matchingSystem.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @PostMapping("/player/add/")
    //该map是一键对应多值
    public String addPlayer(@RequestParam MultiValueMap<String,String> data){
        Integer userId = Integer.parseInt(data.getFirst("user_id"));
        Integer rating = Integer.parseInt(data.getFirst("rating"));

        return matchingService.addPlayer(userId, rating);
    }

    @PostMapping("/player/remove/")
    public String removePlayer(@RequestParam MultiValueMap<String,String> data){
        Integer userId = Integer.parseInt(data.getFirst("user_id"));

        return matchingService.removePlayer(userId);
    }
}
