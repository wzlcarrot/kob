package com.kob.matchingSystem.service;

import org.springframework.stereotype.Service;

@Service
public interface MatchingService {
    String addPlayer(Integer userId, Integer rating, Integer botId);
    String removePlayer(Integer userId);
}

