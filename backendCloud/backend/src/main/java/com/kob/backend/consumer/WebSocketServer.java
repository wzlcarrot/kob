package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    //定义一个全局变量，用于存储session
    /*
    *   Integer:用户id
    *   WebSocketServer:用户对应的WebSocketServer
    * */
    public static ConcurrentHashMap <Integer,WebSocketServer> users = new ConcurrentHashMap<>();

    //用户的信息需要存储到session中
    private Session session = null;

    private User user;

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static BotMapper botMapper;

    //可以让两个springboot进行通信
    public static RestTemplate restTemplate;

    public Game game = null;

    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper){
        WebSocketServer.botMapper = botMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        System.out.println("connected!");
        //此处进行jwt身份验证，如果可以解析出来，则成功，反之异常。
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        System.out.println(user);
        users.put(userId,this);

        if(this.user!=null){
            users.put(userId,this);
        }
        else{
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");

        if(this.user.getId()!= null){
            //删除哈希表的相关信息
            users.remove(this.user.getId());
            //从匹配池中删除

        }
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId){
        User A = userMapper.selectById(aId);
        User B = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);

        Game game = new Game(13,
                14,
                20,
                A.getId(),
                botA,
                B.getId(),
                botB);
        game.createMap();

        //玩家a和b的地图和比赛是一样的
        if(users.get(A.getId())!=null) users.get(A.getId()).game = game;
        if(users.get(B.getId())!=null) users.get(B.getId()).game = game;

        game.start();

        //向前端发送信息
        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());


        JSONObject respA = new JSONObject();
        respA.put("event","start-matching");
        respA.put("opponent_username", B.getUsername());
        respA.put("opponent_photo", B.getPhoto());
        respA.put("game",respGame);
        if(users.get(A.getId())!=null)
        users.get(A.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event","start-matching");
        respB.put("opponent_username", A.getUsername());
        respB.put("opponent_photo", A.getPhoto());
        respB.put("game",respGame);

        if(users.get(B.getId())!=null)
        users.get(B.getId()).sendMessage(respB.toJSONString());

    }

    private void startMatching(Integer botId) {
        System.out.println("user start matching!");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();

        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("bot_id",botId.toString());

        //后端请求匹配系统，添加玩家链接
        restTemplate.postForObject(addPlayerUrl,data,String.class);
    }

    private void stopMatching(){
        System.out.println("user stop matching!");

        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());

        //后端请求匹配系统，删除玩家链接
        restTemplate.postForObject(removePlayerUrl,data,String.class);

    }

    private void move(int direction){

        //如果当前的用户是A
        if(game.getPlayerA().getId().equals(user.getId())){
            //如果是人工的话，
            if(game.getPlayerA().getBotId().equals(-1)) game.setNextStepA(direction);
        }
        else if(game.getPlayerB().getId().equals(user.getId())){
            if(game.getPlayerB().getBotId().equals(-1)) game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从服务端接收client端发送的信息。前端socket.send()发送的信息会在这里被接收
        //将message转化成json对象
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        System.out.println("event:"+event);
        if(event.equals("start-matching")){
            System.out.println("start-matching");
            startMatching(data.getInteger("bot_id"));
        }
        else if(event.equals("stop-matching")){
            System.out.println("stop-matching+111111111111");
            stopMatching();
        }
        else if("move".equals(event)){
            System.out.println("direction:"+data.getInteger("direction"));
            move(data.getInteger("direction"));
        }

        System.out.println("receive message!");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }


    //定义一个从后端向前端发送信息的函数
    public void sendMessage(String message) {
        synchronized (this.session){

            this.session.getAsyncRemote().sendText(message);
        }
    }
}