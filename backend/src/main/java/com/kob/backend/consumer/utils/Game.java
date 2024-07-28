package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import org.apache.ibatis.cache.decorators.BlockingCache;
import org.springframework.security.web.server.header.StrictTransportSecurityServerHttpHeadersWriter;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private Integer rows;
    private Integer cols;
    private Integer inner_walls_count;
    private int g[][];
    private Player playerA,playerB;
    //0 1 2 3表示了四个方向
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing";
    private String loser = ""; //all表示平局，A表示A输，B表示B输

    public Game(Integer rows, Integer cols, Integer inner_walls_count,Integer idA,Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        g = new int[rows][cols];
        //初始化两名玩家，也就是初始化两条蛇
        playerA = new Player(idA,rows-2,1,new ArrayList<>());
        playerB = new Player(idB,1,cols-2,new ArrayList<>());

    }

    public int[][] getG(){
        return g;
    }

    //检查连通性
    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        int dx[] = {-1, 0, 1, 0}, dy[] = {0, 1, 0, -1};
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    //画地图
    private boolean draw() {
        //初始化
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                g[i][j]=0;
            }
        }
        //设置边界
        for(int r = 0; r < this.rows; r++) {

            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for(int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for(int i=0;i<inner_walls_count/2;i++){
            while(true){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if(g[r][c]==1||g[this.rows-1-r][this.cols-1-c]==1){
                    continue;
                }

                if(r==this.rows-2&&c==1||r==1&&c==this.cols-2){
                    continue;
                }

                g[r][c] = g[this.rows-1-r][this.cols-1-c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows-2,1,1,this.cols-2);
    }

    public void createMap(){
        while(true){
            if(draw()){
                break;
            }
        }
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        }
        finally {
            lock.unlock();
        }

    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        }
        finally {
            lock.unlock();
        }
    }

    //判断两名玩家下一步是否合法
    private void judge(){

    }

    //向两个client传递移动信息
    private void sendMove(){
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event","move");
            resp.put("a_direction",nextStepA);
            resp.put("b_direction",nextStepB);

            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        }finally {
            lock.unlock();
        }


    }
    //向两个client公布结果
    private void sendResult(){
        JSONObject resp = new JSONObject();
        resp.put("event","result");

        resp.put("loser",loser);
        sendAllMessage(resp.toJSONString());
    }

    private void sendAllMessage(String message){
        WebSocketServer.users.get(playerA.getId()).sendMessage(message);

        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
    public void run(){
        for(int i=0;i<1000;i++){
            //是否获取了两条蛇下一步操作
            if(nextStep()){
                judge();
                if(status.equals("playing")){
                    sendMove();
                }
                else{
                    sendResult();
                    break;
                }
            }
            else{
                status = "finished";
                lock.lock();
                try{
                    if(nextStepA == null&& nextStepB == null){
                        loser = "all";
                    }
                    else if(nextStepA == null){
                        loser = "A";
                    }
                    else{
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }

                sendResult();
                break;
            }
        }
    }

    //等待两名玩家的下一步操作
    private boolean nextStep(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(int i=0;i<5;i++){
            try{
                Thread.sleep(1000);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getStep().add(nextStepA);
                        playerB.getStep().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();

                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
