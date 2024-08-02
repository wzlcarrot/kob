package com.kob.backend.service.record;


import com.alibaba.fastjson.JSONObject;

public interface GetRecordListService {
    //page:页号
    JSONObject getList(Integer page);
}

