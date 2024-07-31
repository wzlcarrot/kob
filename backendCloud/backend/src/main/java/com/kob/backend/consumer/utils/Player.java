package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;  //-1表示人，正数表示机器
    private String botCode;

    private Integer sx;
    private Integer sy;
    private List<Integer> step;

    //检验该回合，蛇的长度是否增加
    private boolean check_tail_increasing(int step){
        if(step<= 10) return true;

        //每三个回合需要增加长度
        return step%3==1;
    }

    public List<Cell> getCells(){
        List<Cell> res  = new ArrayList<>();
        int dx[] = {-1,0,1,0};
        int dy[] = {0,1,0,-1};
        int s = 0; //表示回合数
        int x = sx,y = sy;
        res.add(new Cell(x,y));

        for(int d:step){
            x+=dx[d];
            y+=dy[d];
            res.add(new Cell(x,y));
            s++;

            //如果蛇不增加长度
            if(!check_tail_increasing(++ s)){
                //去除蛇尾
                res.remove(0);
            }
        }
        return res;
    }

    //将操作步骤转换成字符串
    public String getStepString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int d:step){
            stringBuilder.append(d);
        }

        return stringBuilder.toString();
    }
}
