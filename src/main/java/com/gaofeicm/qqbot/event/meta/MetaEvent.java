package com.gaofeicm.qqbot.event.meta;

import com.gaofeicm.qqbot.event.BaseEvent;
import com.gaofeicm.qqbot.system.MetaEventManger;

public abstract class MetaEvent extends BaseEvent {

    protected void register(MetaEvent event){
        MetaEventManger.register(event);
    }
}
