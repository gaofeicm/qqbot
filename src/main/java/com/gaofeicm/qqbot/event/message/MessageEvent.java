package com.gaofeicm.qqbot.event.message;

import com.gaofeicm.qqbot.event.BaseEvent;
import com.gaofeicm.qqbot.system.MessageEventManger;

public abstract class MessageEvent extends BaseEvent {

    protected void register(MessageEvent event){
        MessageEventManger.register(event);
    }
}
