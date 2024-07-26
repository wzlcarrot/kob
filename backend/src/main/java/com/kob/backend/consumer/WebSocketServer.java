package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private static ConcurrentHashMap <Integer,WebSocketServer> users = new ConcurrentHashMap<>();
   //使用线程安全的set
    private static CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();

    //用户的信息需要存储到session中
    private Session session = null;

    private User user;

    private static UserMapper userMapper;


    @Autowired
    public void setUserMapper(UserMapper userMapper){
        this.userMapper = userMapper;
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
            matchPool.remove(this.user);
        }
    }
    private void startMatching(){
        matchPool.add(this.user);
        System.out.println("user start matching!");

        while(matchPool.size()>=2){
            Iterator<User> iterator = matchPool.iterator();
            User A = iterator.next();
            User B = iterator.next();
            matchPool.remove(A);
            matchPool.remove(B);

            Game game = new Game(13,14,20);
            game.createMap();

            //向前端发送信息
            JSONObject respA = new JSONObject();
            respA.put("event","start-matching");
            respA.put("opponent_username", B.getUsername());
            respA.put("opponent_photo", B.getPhoto());
            respA.put("gameMap",game.getG());
            users.get(A.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event","start-matching");
            respB.put("opponent_username", A.getUsername());
            respB.put("opponent_photo", A.getPhoto());
            respB.put("gameMap",game.getG());
            users.get(B.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopMatching(){
        matchPool.remove(this.user);
        System.out.println("user stop matching!");
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        // 从服务端接收client端发送的信息。前端socket.send()发送的信息会在这里被接收
        //将message转化成json对象
        JSONObject jsonObject = JSONObject.parseObject(message);
        String event = jsonObject.getString("event");
        System.out.println("event:"+event);
        if(event.equals("start-matching")==true){
            System.out.println("start-matching");
            startMatching();
        }
        else{
            System.out.println("stop-matching");
            stopMatching();
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