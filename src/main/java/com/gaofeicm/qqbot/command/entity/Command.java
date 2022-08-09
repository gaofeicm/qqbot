package com.gaofeicm.qqbot.command.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gaofeicm
 */
@Data
public class Command {

    private String bean;

    private String method;

    private boolean isLock;

    private String message;

    private List<Object> option;

    private List<Object> action;

    private JSONObject param;

    public void setOption(Object... option) {
        this.option = List.of(option);
    }

    public void setAction(Object... action) {
        this.action = List.of(action);
    }

    public Command addAction(Object value) {
        if(action == null){
            action = new ArrayList<>();
        }
        action.add(value);
        return this;
    }

    public Command addOptions(Object value) {
        if(option == null){
            option = new ArrayList<>();
        }
        option.add(value);
        return this;
    }

    public Command putParam(String key, Object value) {
        if(param == null){
            param = new JSONObject();
        }
        param.put(key, value);
        return this;
    }
}
