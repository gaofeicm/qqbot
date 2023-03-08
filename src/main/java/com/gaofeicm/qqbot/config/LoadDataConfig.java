package com.gaofeicm.qqbot.config;

import com.alibaba.fastjson2.JSON;
import com.gaofeicm.common.utils.RedisUtil;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.service.CookieService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Component
public class LoadDataConfig implements ApplicationRunner {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private CookieService cookieService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadCk();
    }

    private void loadCk(){
        List<Cookie> cookies = cookieService.getCookieOrderByQq(new HashMap<>(0));
        Map<String, ArrayList<Cookie>> map = new HashMap<>();
        if(cookies != null && cookies.size() > 0) {
            for (Cookie cookie : cookies) {
                if(!map.containsKey(cookie.getQq())){
                    map.put(cookie.getQq(), new ArrayList<>());
                }
                map.get(cookie.getQq()).add(cookie);
            }
        }
        map.forEach((k, v) -> redisUtil.set(k, JSON.toJSONString(v)));
    }
}
