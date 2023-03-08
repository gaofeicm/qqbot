package com.gaofeicm.qqbot.biz;

import com.gaofeicm.qqbot.utils.HttpRequestUtils;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class VpsServerImpl {

    /**
     * 增加余量查询
     * @param url url
     * @return vps
     */
    public StringBuilder getVps(String url){
        String s = HttpRequestUtils.post(url);
        StringBuilder stringBuilder = new StringBuilder("");
        if(s == null || "".equals(s)){
            return stringBuilder.append("未获取到源数据！");
        }
        int datacenter = s.indexOf("datacenter");
        if (datacenter < 0) {
            return stringBuilder.append("未获取到源数据！");
        }
        s = s.substring(datacenter);
        int i1 = s.indexOf("-select-</option>");
        int i2 = s.indexOf("</select>");
        s = s.substring(i1 + 17, i2).trim();
        while(s.contains("<option value=")){
            int a = s.indexOf("\">");
            int b = s.indexOf("</option>");
            stringBuilder.append(s.substring(a + 2, b) + "\r\n");
            s = s.substring(b + 9);
        }
        return stringBuilder;
    }
}
