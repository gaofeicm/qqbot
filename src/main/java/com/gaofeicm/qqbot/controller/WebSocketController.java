package com.gaofeicm.qqbot.controller;

import com.gaofeicm.qqbot.system.EventManager;
import com.gaofeicm.qqbot.utils.CommonUtils;
import com.gaofeicm.qqbot.utils.MessageUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * @author Gaofeicm
 */
@Component
public class WebSocketController implements ApplicationRunner {

    private static String serviceAddress;

    @Value("${serviceAddress}")
    public void setServiceAddress(String serviceAddress) {
        WebSocketController.serviceAddress = serviceAddress;
    }

    public static WebSocketClient webSocketClient() {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI(serviceAddress), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    //MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "[websocket] 建立链接=" + handshake.getHttpStatusMessage());
                }
                @Override
                public void onMessage(String message) {
                    EventManager.post(message);
                }
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "[websocket] 关闭链接，code：" + code + "，reason：" + reason + "，remote：" + remote);
                }
                @Override
                public void onError(Exception ex) {
                    MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "运行错误=" + ex.getMessage());
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
        webSocketClient();
    }
}
