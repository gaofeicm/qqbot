package com.gaofeicm.qqbot.event.request;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.utils.CommonUtils;
import com.gaofeicm.qqbot.utils.MessageUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class FriendAddRequestEvent extends RequestEvent implements ApplicationRunner {
    @Override
    public String getEventName() {
        return "friend";
    }

    @Override
    public void handle(JSONObject message) {
        JSONObject msg = new JSONObject();
        msg.put("action", "set_friend_add_request");
        msg.put("params", new JSONObject(){{
            put("flag", message.getString("flag"));
            put("approve", true);
        }});
        MessageUtils.webSocketClient.send(msg.toString());
        MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "qq：" + message.getString("user_id") + " 已添加机器人为好友！");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        super.register(this);
    }
}
