package com.gaofeicm.qqbot.biz;

import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.service.CookieService;
import com.gaofeicm.qqbot.utils.CommonUtils;
import com.gaofeicm.qqbot.utils.MessageUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Administrator
 */
@Component
@EnableScheduling
public class CronServiceImpl {

    public static boolean checkCkSwitch = true;

    public static boolean checkServerSwitch = true;

    @Resource
    private CookieService cookieService;

    @Resource
    private JdServiceImpl jdService;

    @Resource
    private VpsServerImpl vpsServer;

    @Scheduled(cron = "${task.cron.ck.checkLogin}")
    public void cronCheckCkLogin(){
        if(!checkCkSwitch){
            return;
        }
        //MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(),  SDF.format(new Date()) + "：开始检查ck有效性");
        //先检查是否过期
        this.checkAccountExp();
        List<Cookie> cookies = cookieService.getCookie(new HashMap<String, Object>(1){{put("available", "1");}});
        List<String> qqs = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        cookies.forEach(cookie -> {
            JSONObject o = jdService.checkLogin(cookie.getCookie());
            if(o != null){
                if (o.getIntValue("islogin") == 1) {
                    //正常
                }else if (o.getIntValue("islogin") == 0) {
                    Cookie ck = new Cookie();
                    ck.setId(cookie.getId());
                    ck.setAvailable(0);
                    cookieService.saveCookie(ck);
                    MessageUtils.sendPrivateMsg(cookie.getQq(), "您的账号【" + cookie.getPtPin() + "】已过期，请重新获取ck后提交！");
                    count.getAndIncrement();
                    qqs.add(cookie.getQq());
                }else{
                    MessageUtils.sendPrivateMsg(cookie.getQq(), "账号状态未知，请联系管理员！");
                }
            }
        });
        if(count.get() > 0){
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "本轮账号有效性检测共有" + count + "个账号失效！分别为：\r\n" + String.join("，", qqs));
        }
    }

    @Scheduled(cron = "${task.cron.ck.checkLogout}")
    public void cronCheckCkLogout(){
        if(!checkCkSwitch){
            return;
        }
        List<Cookie> cookies = cookieService.getCookie(new HashMap<String, Object>(1){{put("available", "0");}});
        List<String> qqs = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        cookies.forEach(cookie -> {
            long exp = cookie.getExpirationTime().getTime();
            long time = System.currentTimeMillis();
            if(exp > time){
                MessageUtils.sendPrivateMsg(cookie.getQq(), "您的账号【" + cookie.getPtPin() + "】已过期，请重新获取ck后提交！");
                count.getAndIncrement();
                qqs.add(cookie.getQq());
            }
        });
        if(count.get() > 0){
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "本轮检查服务未到期但账号到期的任务共有" + count + "个账号符合！分别为：\r\n" + String.join("，", qqs));
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkService(){
        if (!checkServerSwitch) {
            return;
        }
        try {
            StringBuilder vps = vpsServer.getVps("https://hax.co.id/create-vps/");
            if (vps.length() != 0) {
                MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), vps.toString());
            }
        }catch (Exception e){
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "自动检查余量出现了异常！" + e.getMessage());
        }
    }

    /**
     * 检查账号到期未禁用的
     */
    private void checkAccountExp(){
        List<Map<String, Object>> cookies = cookieService.getExpCookie();
        List<String> qqs = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        cookies.forEach(cookie -> {
            Cookie ck = new Cookie();
            ck.setId(cookie.get("id").toString());
            ck.setAvailable(0);
            cookieService.saveCookie(ck);
            MessageUtils.sendPrivateMsg(cookie.get("qq").toString(), "您的代挂服务已到期，请重新续费后联系管理员延期再使用！");
            count.getAndIncrement();
            qqs.add(cookie.get("qq").toString());
        });
        if(count.get() > 0){
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "本轮检查账号到期未禁用的任务共有" + count + "个账号失效！分别为：\r\n" + String.join("，", qqs));
        }
    }
}
