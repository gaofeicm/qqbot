package com.gaofeicm.qqbot.biz;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.utils.HttpRequestUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

/**
 * @author Gaofeicm
 */
@Service
public class JpkServiceImpl {

    @Value("${jpk.appid}")
    private String appId;

    @Value("${jpk.appKey}")
    private String appKey;

    @Value("${jpk.unionId}")
    private String unionId;

    /**
     * 转链
     * @param content content
     * @return 转链
     */
    @SneakyThrows
    public JSONObject getyh(String content){
        String url = "https://api.jingpinku.com/get_atip_link/api?appid=" + appId + "&appkey=" + appKey + "&union_id=" + unionId + "&content=" + URLEncoder.encode(content);
        String s = HttpRequestUtils.doGet(url);
        return JSONObject.parseObject(s);
    }
}
