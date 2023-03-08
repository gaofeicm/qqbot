package com.gaofeicm.qqbot.event.notice;

import com.gaofeicm.qqbot.event.BaseEvent;
import com.gaofeicm.qqbot.system.NoticeEventManger;

/**
 * @author Administrator
 */
public abstract class NoticeEvent extends BaseEvent {

    protected void register(NoticeEvent event){
        NoticeEventManger.register(event);
    }
}
