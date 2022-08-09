package com.gaofeicm.qqbot.utils;

import com.alibaba.fastjson2.JSONObject;
import org.java_websocket.client.WebSocketClient;

public class MessageUtils {

    public static WebSocketClient webSocketClient;

    /**
     * 发送私聊消息
     * @param userId 用户id
     * @param message 消息内容
     */
    public static void sendPrivateMsg(String userId, String message){
        JSONObject msg = new JSONObject();
        msg.put("action", "send_private_msg");
        msg.put("params", new JSONObject(){{
            put("user_id", userId);
            put("message", message);
        }});
        webSocketClient.send(msg.toString());
    }
}
