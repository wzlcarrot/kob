package com.kob.botRunningSystem.service;

import org.springframework.stereotype.Service;

@Service
public interface BotRunningService {

    String addBot(Integer userId, String botCode, String input);


}
