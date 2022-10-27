package com.gaofeicm.qqbot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.biz.JpkServiceImpl;
import com.gaofeicm.qqbot.utils.CommonUtils;
import com.gaofeicm.qqbot.utils.JpkMessageUtils;
import com.gaofeicm.qqbot.utils.MessageUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Properties;

/**
 * @author Gaofeicm
 */
@Component
public class JpkWebSocketController implements ApplicationRunner {

    private static String serviceAddress;

    private static JpkServiceImpl jpkService;

    private static boolean enable = true;

    @Value("${jpkServiceEnable}")
    public static void setEnable(boolean enable) {
        JpkWebSocketController.enable = enable;
    }

    @Resource
    public void setJpkService(JpkServiceImpl jpkService) {
        JpkWebSocketController.jpkService = jpkService;
    }

    @Value("${jpkServiceAddress}")
    public void setServiceAddress(String serviceAddress) {
        JpkWebSocketController.serviceAddress = serviceAddress;
    }

    public static void webSocketClient() {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI(serviceAddress), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    //MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "[websocket] 建立链接=" + handshake.getHttpStatusMessage());
                }
                @Override
                public void onMessage(String message) {
                    JpkWebSocketController.handle(message);
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
            if(enable) {
                webSocketClient.connect();
                JpkMessageUtils.webSocketClient = webSocketClient;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClassPathResource resource = new ClassPathResource("application.yml");
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        String property = properties.getProperty("jpkServiceEnable", "true");
        setEnable(Boolean.parseBoolean(property));
        webSocketClient();
    }

    private static void handle(String message){
        JSONObject msgObject = JSONObject.parseObject(message);
        String postType = msgObject.getString("post_type");
        if(postType != null && "message".equals(postType)){
            String subType = msgObject.getString("sub_type");
            if(subType != null && "friend".equals(subType)){
                String msg = msgObject.getString("message").trim();
                String qq = msgObject.getString("user_id");
                JSONObject yh = jpkService.getyh(msg);
                if (yh != null && yh.getIntValue("code") == 0) {
                    if(!msg.startsWith("http")){
                        JpkMessageUtils.sendPrivateMsg(qq, yh.getString("content"));
                    }else{
                        JpkMessageUtils.sendPrivateMsg(qq, yh.getString("official"));
                    }
                    JpkMessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "qq：" + qq + "，使用了优惠券查询功能，关键词为：" + msg + "，转换的链接为：" + yh.getString("content"));
                }else {
                    JpkMessageUtils.sendPrivateMsg(qq, "链接有误，请检查链接地址是否正确，或者联系管理员！");
                    JpkMessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "qq：" + qq + "，使用了优惠券查询功能，但是没有查到数据！关键词为：" + msg);
                }
            }
        }
    }
}
