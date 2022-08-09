package com.gaofeicm.qqbot.event;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.boot.ApplicationRunner;

public abstract class BaseEvent implements ApplicationRunner {

    public abstract String getEventName();

    public abstract void handle(JSONObject message);
}
