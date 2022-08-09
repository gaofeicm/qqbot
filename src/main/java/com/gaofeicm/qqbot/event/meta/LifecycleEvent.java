package com.gaofeicm.qqbot.event.meta;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LifecycleEvent extends MetaEvent implements ApplicationRunner {

    @Override
    public String getEventName() {
        return "lifecycle";
    }

    @Override
    public void handle(JSONObject message) {
        //MessageUtils.sendPrivateMsg("952219232", "机器人已上线！");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        super.register(this);
    }
}
