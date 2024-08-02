package com.kob.backend.service.rankList;

import com.alibaba.fastjson.JSONObject;

public interface GetRankListService {
    JSONObject getList(Integer page);
}
