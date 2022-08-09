package com.gaofeicm.qqbot.command;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.service.CookieServiceImpl;
import com.gaofeicm.qqbot.utils.CookieUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Gaofeicm
 */
@Service
public class CommandService {

    @Resource
    private CookieServiceImpl cookieService;

    /**
     * 设置ck一个月有效
     * @param param 参数
     */
    public JSONObject setCookieExpForMonth(JSONObject param){
        return this.setCookieExp(param, 1);
    }

    /**
     * 设置ck一年有效
     * @param param 参数
     */
    public JSONObject setCookieExpForYear(JSONObject param){
        return this.setCookieExp(param, 12);
    }

    /**
     * 拒绝账号通过
     * @param param 参数
     * @return json
     */
    public JSONObject refuseCookie(JSONObject param){
        JSONArray message = new JSONArray();
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", "您已拒绝该账号的申请！");
        }});
        message.add(new JSONObject(){{
            put("to", param.getString("from"));
            put("msg", "管理员拒绝了您的申请！请联系管理员！");
        }});
        param.put("message", message);
        return param;
    }

    /**
     * 设置ck过期时间
     * @param param 参数
     * @param month 月份
     * @return 参数
     */
    private JSONObject setCookieExp(JSONObject param, int month){
        JSONArray message = new JSONArray();
        Date expDate = this.subMonth(new Date(), month);
        Cookie cookie = new Cookie();
        cookie.setId(param.getString("id"));
        cookie.setAvailable(1);
        cookie.setExpirationTime(expDate);
        int i = cookieService.saveCookie(cookie);
        if(i > 0) {
            String toMessage = "QQ:" + param.getString("from") + "," + CookieUtils.getPin(param.getString("ck"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expDateStr = sdf.format(expDate);
            message.add(new JSONObject() {{
                put("to", param.getString("to"));
                put("msg", toMessage + " 已启用!有效期:" + month + "个月,到期时间:" + expDateStr);
            }});
            message.add(new JSONObject() {{
                put("to", param.getString("from"));
                put("msg", "管理员通过了您的申请！有效期:" + month +"个月,到期时间:" + expDateStr);
            }});
        }else{
            message.add(new JSONObject(){{
                put("to", param.getString("to"));
                put("msg", "启用账号出错了！");
            }});
        }
        param.put("message", message);
        return param;
    }

    /****
     * 传入具体日期 ，返回具体日期增加n个月。
     * @param date 时间
     * @param month 差值
     * @return date
     */
    private Date subMonth(Date date, int month) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.MONTH, month);
        return rightNow.getTime();
    }

}
