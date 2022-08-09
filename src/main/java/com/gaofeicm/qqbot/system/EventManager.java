package com.gaofeicm.qqbot.system;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.event.BaseEventManager;

import java.util.HashMap;
import java.util.Map;

public class EventManager {

    private static final Map<String, BaseEventManager> EVENT_MANAGER_MAP = new HashMap<>(4){{
        put("message", new MessageEventManger());
        put("request", new RequestEventManger());
        put("notice", new NoticeEventManger());
        put("meta_event", new MetaEventManger());
    }};

    /**
     * 处理消息
     * @param message 消息内容
     */
    public static void post(String message){
        JSONObject msgObject = JSONObject.parseObject(message);
        String postType = msgObject.getString("post_type");
        BaseEventManager event = EVENT_MANAGER_MAP.get(postType);
        if(event == null){
            System.out.println("unsupported post_type type:" + postType);
        }else{
            event.handle(msgObject);
        }
    }
}
