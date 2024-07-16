package com.kob.backend.service.user.account;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface InfoService {

    public Map<String,String> getInfo();

}
