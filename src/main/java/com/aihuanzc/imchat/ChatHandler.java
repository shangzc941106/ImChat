package com.aihuanzc.imchat;
/**
 * @Copyright 源码阅读网 http://coderead.cn
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author lbds
 * @date 2020/11/14 6:17 PM
 */
@ServerEndpoint("/chat/{uid}")
@Controller
public class ChatHandler {
    //初始化进入的方法接口
    @RequestMapping("/")
    public String root() {
        return "redirect:/chat.html";
    }

    // 当前在线 用户
    static Map<String, Session> sessions = new ConcurrentHashMap<>();
    //头像图标，随机选择
    String[] head_image = new String[]{"christian.jpg",
            "daniel.jpg",
            "elliot.jpg",
            "helen.jpg",
            "jenny.jpg",
            "lena.png",
            "lindsay.png",
            "mark.png",
            "matt.jpg",
            "molly.png",
            "steve.jpg",
            "stevie.jpg",
            "tom.jpg",
            "veronika.jpg"};

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        // 用户上线
        // 随机生成头象
        if (ChatHandler.sessions.containsKey(uid)) {
            System.err.println("用户已存在：" + uid);
            Map<String, String> map = new HashMap<>();
            map.put("type", "err_user_exist");
            try {
                session.getBasicRemote().sendText(toJson(map));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        String header = head_image[new Random().nextInt(head_image.length)];
        User user = new User(header, uid);
        session.getUserProperties().put("user", user);
        ChatHandler.sessions.put(uid, session);
        fireUpdateUsers();
    }

    // 更新用户列表
    private void fireUpdateUsers() {
        List<User> userList = sessions.values().stream().map(s -> (User) s.getUserProperties().get("user")).collect(Collectors.toList());
        Map<String, Object> message = new HashMap<>();
        message.put("type", "update_user");
        message.put("users", userList);
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 给所有用户发送消息
        for (Session session : sessions.values()) {
            try {
                session.getBasicRemote().sendText(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClose
    public void onClose(Session session) {
        User user = (User) session.getUserProperties().get("user");
        sessions.remove(user.name);
        fireUpdateUsers();
    }

    private String toJson(Object value) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    @OnMessage
    public void onMessage(Session currentSession, String msg) throws IOException {
        Map<String, Object> message = new HashMap<>();
        if (msg.startsWith("$img")) {
            message.put("type", "img_msg"); //普通消息
            message.put("msg", msg.substring(4));
        } else {
            message.put("type", "normal_msg"); //普通消息
            message.put("msg", msg);
        }
        message.put("user", currentSession.getUserProperties().get("user"));
        // 接收消息
        for (Session session : sessions.values()) {
            session.getBasicRemote().sendText(toJson(message));
        }
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    public static class User implements Serializable {
        public String head;
        public String name;

        public User(String header, String name) {
            this.head = header;
            this.name = name;
        }
    }
}
