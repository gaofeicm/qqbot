package com.gaofeicm.qqbot.system;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.event.BaseEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gaofeicm
 */
public class EventManager {

    private final static Logger log = LoggerFactory.getLogger(EventManager.class);

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
            if(!"0".equals(msgObject.getString("retcode"))) {
                log.warn("unsupported post_type type:" + postType);
            }
        }else{
            event.handle(msgObject);
        }
    }
}
