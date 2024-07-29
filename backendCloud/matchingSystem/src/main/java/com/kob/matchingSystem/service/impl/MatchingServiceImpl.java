package com.kob.matchingSystem.service.impl;
import com.kob.matchingSystem.service.MatchingService;
import com.kob.matchingSystem.service.impl.util.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {

    public static final MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addPlayer(Integer userId, Integer rating) {
        matchingPool.addPlayer(userId,rating);
        System.out.println("add "+userId+" "+rating);
        return "add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        matchingPool.removePlayer(userId);
        System.out.println("remove "+userId);
        return "remove player success";
    }
}
