package com.gaofeicm.qqbot.event.meta;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class HeartbeatEvent extends MetaEvent implements ApplicationRunner {

    @Override
    public String getEventName() {
        return "heartbeat";
    }

    @Override
    public void handle(JSONObject message) {
        //MessageUtils.sendPrivateMsg("952219232", "心跳！");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        super.register(this);
    }
}
