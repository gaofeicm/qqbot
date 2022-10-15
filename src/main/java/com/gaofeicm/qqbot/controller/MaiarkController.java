package com.gaofeicm.qqbot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gaofeicm.qqbot.utils.CommonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@RestController
public class MaiarkController {

    @PostMapping("api/envs")
    public JSONObject addEvn(HttpServletRequest request, @RequestBody List<Map<String, Object>> params){
        if(params != null){
            params.forEach(o -> {
                JSONObject obj = JSONObject.parseObject(JSON.toJSONString(o));
                JSONObject parameter = CommonUtils.getAllRequestParam(request).fluentPutAll(obj);
                System.out.println(parameter.toJSONString());
            });
        }else{
            System.out.println("参数为空！");
        }
        return new JSONObject(){{
           put("code", 200);
        }};
    }

    @GetMapping("open/auth/token")
    public JSONObject auth(HttpServletRequest request){
        JSONObject parameter = CommonUtils.getAllRequestParam(request);
        System.out.println(parameter.toJSONString());
        return new JSONObject(){{
            put("code", 200);
            put("data", new JSONObject(){{
                put("token", "2eab6c9c-8879-4bf5-ab55-eef6103e5b70");
                put("token_type", "Bearer");
                put("expiration", (System.currentTimeMillis() + (1000L * 30 * 24 * 60 * 60)) / 1000);
            }});
        }};
    }

}
