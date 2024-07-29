package com.kob.matchingSystem.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{

    private static List<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    private static RestTemplate restTemplate;
    private static final String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";


    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId,Integer rating){
        lock.lock();
        try {
            players.add(new Player(userId,rating,0));
        }finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId){
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>();
            for(Player player:players){
                if(player.getUserId().equals(userId)==false){
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        }finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime(){ //将所有玩家的等待时间加1
        for(Player player:players){
            player.setWaitingTime(player.getWaitingTime()+1);
        }
    }

    //判断两名玩家是否匹配成功
    private boolean checkMatched(Player a,Player b){
        int ratingDelta = Math.abs(a.getRating()-b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(),b.getWaitingTime());

        return ratingDelta<=waitingTime*10;
    }

    //返回a和b的匹配结果
    private void sendResult(Player a,Player b){
        System.out.println("Matched: "+a.getUserId()+" "+b.getUserId());
        //向游戏服务器发送匹配结果
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("a_id",a.getUserId().toString());
        data.add("b_id",b.getUserId().toString());

        //把玩家匹配结果发送到另外要给springboot程序
        restTemplate.postForObject(startGameUrl,data,String.class);
    }
    //尝试匹配所有玩家
    private void matchPlayers(){
        System.out.println("Matching players..."+players);
        boolean used[] = new boolean[players.size()];
        for(int i=0;i<players.size();i++){
            if(used[i]) continue;

            for(int j=i+1;j<players.size();j++){
                if(used[j]) continue;
                Player a = players.get(i),b = players.get(j);
                 if(checkMatched(a,b)){
                     used[i] = used[j] = true;
                      sendResult(a,b);
                      break;
                 }
            }
        }

        //匹配完之后，删掉已经匹配的玩家
        List<Player> newPlayers = new ArrayList<>();
        for(int i=0;i<players.size();i++){
            if(used[i]==false){
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                }finally {
                    lock.unlock();
                }


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
