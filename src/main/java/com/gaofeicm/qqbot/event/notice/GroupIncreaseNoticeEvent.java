package com.gaofeicm.qqbot.event.notice;

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
public class GroupIncreaseNoticeEvent extends NoticeEvent implements ApplicationRunner {
    @Override
    public String getEventName() {
        return "group_increase";
    }

    @Override
    public void handle(JSONObject message) {
        MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "qq：" + message.getString("user_id") + " 已加入群：" + message.getString("group_id") + "！");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        super.register(this);
    }
}
