package com.gaofeicm.qqbot.controller;

import com.gaofeicm.qqbot.system.EventManager;
import com.gaofeicm.qqbot.utils.MessageUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class WebSocketController implements ApplicationRunner {

    @Value("${serviceAddress}")
    private String serviceAddress;

    public WebSocketClient webSocketClient() {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI(serviceAddress), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    //System.out.println("[websocket] 连接成功");
                }
                @Override
                public void onMessage(String message) {
                    System.out.println("[websocket] 收到消息" + message);
                    EventManager.post(message);
                }
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    //System.out.println("[websocket] 退出连接");
                }
                @Override
                public void onError(Exception ex) {
                    System.out.println("[websocket] 连接错误=" + ex.getMessage());

                }
            };
            webSocketClient.connect();
            MessageUtils.webSocketClient = webSocketClient;
            return webSocketClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.webSocketClient();
    }
}
