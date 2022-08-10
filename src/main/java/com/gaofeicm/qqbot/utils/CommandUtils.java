package com.gaofeicm.qqbot.utils;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.command.entity.Command;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Gaofeicm
 */
public class CommandUtils {

    /**
     * 待处理的命令
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<Command>> CMD = new ConcurrentHashMap<>();

    /**
     * 添加命令
     * @param qq qq
     * @param command 命令
     */
    public static void addCommand(String qq, Command command){
        CopyOnWriteArrayList<Command> commands = CMD.get(qq);
        if(commands != null){
            commands.add(command);
        }else {
            commands = new CopyOnWriteArrayList<>();
            commands.add(command);
        }
        CMD.put(qq, commands);
    }

    /**
     * 按QQ获取命令
     * @param qq qq
     * @return 命令
     */
    public static List<Command> getCommand(String qq){
        return CMD.get(qq);
    }

    /**
     * 按QQ获取命令
     * @param qq qq
     * @return 命令
     */
    public static boolean removeUserCommand(String qq, Object o){
        return CMD.get(qq).remove(o);
    }

    public static Command getDefaultCommand(String message){
        Command command = new Command();
        command.setBean("commandService");
        command.setMessage(message);
        return command;
    }

    public static JSONObject getQlToken(String url){
        String s = HttpRequestUtils.doGet(url);
        if(s != null){
            JSONObject jsonObject = JSONObject.parseObject(s);
            if(jsonObject.getIntValue("code") == 200){
                return jsonObject.getJSONObject("data");
            }
        }
        return null;
    }
}
