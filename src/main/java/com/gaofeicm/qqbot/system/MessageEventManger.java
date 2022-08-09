package com.gaofeicm.qqbot.system;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.event.BaseEventManager;
import com.gaofeicm.qqbot.event.message.MessageEvent;

import java.util.HashMap;
import java.util.Map;

public class MessageEventManger implements BaseEventManager {

    private static final Map<String, MessageEvent> EVENT_MAP = new HashMap<>();

    @Override
    public void handle(JSONObject message){
        String postType = message.getString("sub_type");
        MessageEvent event = EVENT_MAP.get(postType);
        if(event == null){
            System.out.println("unsupported message type:" + postType);
        }else{
            event.handle(message);
        }
    }

    /**
     * 注册
     * @param event 事件
     */
    public static void register(MessageEvent event){
        EVENT_MAP.put(event.getEventName(), event);
    }

}
