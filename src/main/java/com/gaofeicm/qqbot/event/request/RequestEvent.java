package com.gaofeicm.qqbot.event.request;

import com.gaofeicm.qqbot.event.BaseEvent;
import com.gaofeicm.qqbot.system.RequestEventManger;

/**
 * @author Administrator
 */
public abstract class RequestEvent extends BaseEvent {

    protected void register(RequestEvent event){
        RequestEventManger.register(event);
    }
}
