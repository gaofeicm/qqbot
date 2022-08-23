package com.gaofeicm.qqbot.utils;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.controller.WebSocketController;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gaofeicm
 */
public class MessageUtils {

    private final static Logger log = LoggerFactory.getLogger(MessageUtils.class);

    public static WebSocketClient webSocketClient;

    private static boolean isLock = false;

    /**
     * 发送私聊消息
     * @param userId 用户id
     * @param message 消息内容
     */
    public static void sendPrivateMsg(String userId, String message){
        if(isLock){
            return;
        }
        JSONObject msg = new JSONObject();
        msg.put("action", "send_private_msg");
        msg.put("params", new JSONObject(){{
            put("user_id", userId);
            put("message", message);
        }});
        try{
            send(msg.toString(), userId, message);
        }catch (WebsocketNotConnectedException e){
            isLock = true;
            reConnection(msg.toString(), userId, message);
        }
    }

    private static void reConnection(String msg, String userId, String message){
        try {
            if(!MessageUtils.webSocketClient.isOpen()){
                WebSocketController.webSocketClient();
            }
            Thread.sleep(1 * 1000);
            send(msg, userId, message);
            isLock = false;
        }catch (Exception e){
            try {
                log.error("websocket重连失败！等待10s后再次连接" + e.getMessage());
                Thread.sleep(10 * 1000);
                reConnection(msg, userId, message);
            }catch (Exception ex){
                log.error("休眠异常！" + ex.getMessage());
            }
        }
    }

    private static void send(String msg, String userId, String message){
        webSocketClient.send(msg);
        log.info("发送给QQ：" + userId + "，内容为：" + message);
    }
}
