package com.kob.matchingSystem.service;

import org.springframework.stereotype.Service;

@Service
public interface MatchingService {

    //在匹配池中添加一名玩家
    public String addPlayer(Integer userId,Integer rating);

    //从匹配池中删除一名玩家
    public String removePlayer(Integer userId);
}
