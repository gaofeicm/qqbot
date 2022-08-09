package com.gaofeicm.qqbot.event;

import com.alibaba.fastjson2.JSONObject;

public interface BaseEventManager{

    public void handle(JSONObject message);
}
