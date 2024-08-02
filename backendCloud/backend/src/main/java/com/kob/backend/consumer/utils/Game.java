package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.apache.ibatis.cache.decorators.BlockingCache;
import org.springframework.security.web.server.header.StrictTransportSecurityServerHttpHeadersWriter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Bot botA, Integer idB, Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        g = new int[rows][cols];
        Integer botIdA = -1;
        Integer botIdB = -1;
        String botCodeA = "";
        String botCodeB = "";
        if(botA!=null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }

        if(botB!=null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        //初始化两名玩家，也就是初始化两条蛇
        playerA = new Player(idA,botIdA,botCodeA,rows-2,1,new ArrayList<>());
        playerB = new Player(idB,botIdB,botCodeB,1,cols-2,new ArrayList<>());

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

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        if(n==0) return false;
        Cell cell = cellsA.get(n - 1);
        if (g[cell.x][cell.y] == 1) return false;

        for (int i = 0; i < n - 1; i ++ ) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }

        for (int i = 0; i < n - 1; i ++ ) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }

        return true;
    }



    //判断两名玩家下一步是否合法
    private void judge(){
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA,cellsB);
        boolean validB = check_valid(cellsB,cellsA);

        if(!validA||!validB){
            status = "finished";

            //判断游戏的胜负
            if(validA==false&&validB==false){
                loser = "all";
            }
            else if(!validA){
                loser = "A";
            }
            else{
                loser = "B";
            }
        }
    }

    //向两个client传递移动信息
    private void sendMove() {  // 向两个Client传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);

            sendAllMessage(resp.toJSONString());
            System.out.println("resp:"+resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    //将地图信息转化成字符串
    private String getMapString(){
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<this.rows;i++){
            for(int j=0;j<this.cols;j++){
                stringBuilder.append(g[i][j]);
            }
        }

        return stringBuilder.toString();
    }

    //更新用户的天梯积分
    private void updateUserRating(Player player,Integer rating){
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabase(){

        //游戏结束，更新积分
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if(loser=="A"){
            ratingA+=10;
            ratingB-=10;
        }
        else if(loser=="B"){
            ratingA+=10;
            ratingB-=10;
        }

        updateUserRating(playerA,ratingA);
        updateUserRating(playerB,ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),

                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),

                playerA.getStepString(),
                playerB.getStepString(),

                getMapString(),

                loser,

                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    //向两个client公布结果
    private void sendResult() {  // 向两个Client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);

        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }



    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    @Override
    public void run() {
        System.out.println("status:"+status);
        for (int i = 0; i < 1000; i ++ ) {
            if (nextStep()) {  // 是否获取了两条蛇的下一步操作
                judge();
                if (status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }

    //将当前的局面信息，编成字符串
    private String getInput(Player player) {  // 将当前的局面信息，编码成字符串
        Player me, you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepString() + ")";
    }


    private void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) return;  // 亲自出马，不需要执行代码
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }


    //等待两名玩家的下一步操作
    private boolean nextStep(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for(int i=0;i<50;i++){
            try{
                Thread.sleep(100);
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
