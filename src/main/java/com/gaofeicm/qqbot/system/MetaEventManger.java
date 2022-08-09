package com.gaofeicm.qqbot.system;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.event.BaseEventManager;
import com.gaofeicm.qqbot.event.meta.MetaEvent;

import java.util.HashMap;
import java.util.Map;

public class MetaEventManger implements BaseEventManager{

    private static final Map<String, MetaEvent> EVENT_MAP = new HashMap<>();

    @Override
    public void handle(JSONObject message) {
        String metaEventType = message.getString("meta_event_type");
        MetaEvent event = EVENT_MAP.get(metaEventType);
        if(event == null){
            System.out.println("unsupported message type:" + metaEventType);
        }else{
            event.handle(message);
        }
    }

    /**
     * 注册
     * @param event 事件
     */
    public static void register(MetaEvent event){
        EVENT_MAP.put(event.getEventName(), event);
    }
}
