package com.gaofeicm.qqbot.service;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.utils.HttpRequestUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Gaofeicm
 */
@Service
public class BotServiceImpl {

    public void QqRobotEvenHandle(HttpServletRequest request) {
        JSONObject jsonParam = this.getJSONParam(request);
        System.out.println("接收参数为:{}" + jsonParam.toString());
        if("message".equals(jsonParam.getString("post_type"))){
            String message = jsonParam.getString("message");
            if("你好".equals(message)){
                // user_id 为QQ好友QQ号
                String url = "http://127.0.0.1:5700/send_private_msg?user_id=xxxxx&message=你好~";
                String result = HttpRequestUtils.doGet(url);
                System.out.println("发送成功:==>" + result);
            }
        }
    }

    public JSONObject getJSONParam(HttpServletRequest request) {
        JSONObject jsonParam = null;
        try {
            // 获取输入流
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            // 数据写入Stringbuilder
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = streamReader.readLine()) != null) {
                sb.append(line);
            }
            jsonParam = JSONObject.parseObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonParam;
    }

}
