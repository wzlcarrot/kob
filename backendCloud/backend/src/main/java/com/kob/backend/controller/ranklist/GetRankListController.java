package com.kob.backend.controller.ranklist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.rankList.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRankListController {

    @Autowired
    private GetRankListService getRankListService;

    @GetMapping("rankList/getList/")
    public JSONObject getList(@RequestParam Map<String,String> data){
        Integer page = Integer.parseInt(data.get("page"));
        return getRankListService.getList(page);
    }
}
