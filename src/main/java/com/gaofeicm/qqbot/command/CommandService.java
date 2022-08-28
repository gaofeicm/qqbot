package com.gaofeicm.qqbot.command;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.command.entity.Command;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.entity.Ql;
import com.gaofeicm.qqbot.entity.QlCookie;
import com.gaofeicm.qqbot.service.CookieService;
import com.gaofeicm.qqbot.service.QlCookieService;
import com.gaofeicm.qqbot.service.QlService;
import com.gaofeicm.qqbot.utils.CommandUtils;
import com.gaofeicm.qqbot.utils.CommonUtils;
import com.gaofeicm.qqbot.utils.CookieUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Gaofeicm
 */
@Service
public class CommandService {

    @Resource
    private CookieService cookieService;

    @Resource
    private QlService qlService;

    @Resource
    private QlCookieService qlCookieService;

    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        Date expDate = CommonUtils.subMonth(new Date(), month);
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
                put("msg", "账号加入代挂！有效期:" + month +"个月,到期时间:" + expDateStr);
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

    /**
     * 添加青龙面板引导信息
     * @param param 参数
     * @return json
     */
    public JSONObject addQlStr(JSONObject param){
        JSONArray message = new JSONArray();
        StringBuilder msg = new StringBuilder();
        msg.append("请按下面模板完善json:\r\n");
        msg.append("addQl{\"name\":\"haxvz\",\"address\":\"http://haxvz.gaofeicm.cf:8080\",\"clientId\":\"\",\"clientSecret\":\"\",\"weigth\":\"9\",\"maxCount\":100}");
        Command command = CommandUtils.getDefaultCommand(msg.toString());
        command.setOption("addQl");
        command.setAction("addQl");
        command.putParam("from", param.getString("to")).putParam("to", param.getString("to"));
        CommandUtils.addCommand(param.getString("to"), command);
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", msg.toString());
        }});
        param.put("message", message);
        return param;
    }

    /**
     * 添加青龙面板
     * @param param 参数
     * @return json
     */
    public JSONObject addQl(JSONObject param){
        JSONArray message = new JSONArray();
        StringBuilder msg = new StringBuilder();
        String m = param.getString("msg").substring(5);
        Ql ql = JSONObject.parseObject(m, Ql.class);
        JSONObject qlToken = CommandUtils.getQlToken(ql.getAddress() + "/open/auth/token?client_id=" + ql.getClientId() + "&client_secret=" + ql.getClientSecret());
        if(qlToken == null){
            msg.append("配置有误,无法获取登录凭证!");
        }else{
            ql.setTokeType(qlToken.getString("token_type"));
            ql.setToken(qlToken.getString("token"));
            int i = qlService.saveQl(ql);
            if(i > 0){
                msg.append("添加成功！青龙面板的id为：" + ql.getId());
            }else{
                msg.append("添加失败！请查看日志！");
            }
        }
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", msg.toString());
        }});
        param.put("message", message);
        return param;
    }

    /**
     * 查询青龙面板
     * @param param 参数
     * @return json
     */
    public JSONObject findQl(JSONObject param){
        JSONArray message = new JSONArray();
        List<Ql> qls = qlService.getQl(new HashMap<>(0));
        StringBuilder msg = new StringBuilder("----------------------------面板列表----------------------\r\n");
        msg.append("序号  id                   名称     地址                                                ck数量  最大数量  权重\r\n");
        for (int i = 0; i < qls.size(); i++) {
            Ql ql = qls.get(i);
            msg.append(i + 1).append("       ").append(ql.getId()).append("           ").append(ql.getName()).append("   ").append(ql.getAddress()).append("    ").append(ql.getCookieCount()).append("         ").append(ql.getMaxCount()).append("       ").append(ql.getWeigth()).append("\r\n");
        }
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", msg.toString());
        }});
        param.put("message", message);
        return param;
    }

    /**
     * 查询青龙面板
     * @param param 参数
     * @return json
     */
    public JSONObject findCookie(JSONObject param){
        JSONArray message = new JSONArray();
        List<Cookie> cookies = cookieService.getCookie(new HashMap<>(0));
        StringBuilder msg = new StringBuilder("----------------------------cookie列表----------------------\r\n");
        msg.append("序号  QQ                  pin                              启用    到期时间\r\n");
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            msg.append(i + 1).append("      ").append(cookie.getQq()).append("    ").append(cookie.getPtPin(), 7, cookie.getPtPin().length() - 1).append("       ").append((boolean)cookie.getAvailable() ? "是" : "否").append("  ").append(SDF.format(cookie.getExpirationTime())).append("\r\n");
        }
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", msg.toString());
        }});
        param.put("message", message);
        return param;
    }

    /**
     * 添加青龙面板引导信息
     * @param param 参数
     * @return json
     */
    public JSONObject addQlCookieStr(JSONObject param){
        JSONArray message = new JSONArray();
        StringBuilder msg = new StringBuilder();
        msg.append("请按下面模板完善json:\r\n");
        msg.append("addQlCookie{\"qlId\":\"1556920808510341122\",\"cookieId\":\"1556554834355539970\"}");
        Command command = CommandUtils.getDefaultCommand(msg.toString());
        command.setOption("addQlCookie");
        command.setAction("addQlCookie");
        command.putParam("from", param.getString("to")).putParam("to", param.getString("to"));
        CommandUtils.addCommand(param.getString("to"), command);
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", msg.toString());
        }});
        param.put("message", message);
        return param;
    }

    /**
     * 添加青龙面板
     * @param param 参数
     * @return json
     */
    public JSONObject addQlCookie(JSONObject param){
        JSONArray message = new JSONArray();
        StringBuilder msg = new StringBuilder();
        String m = param.getString("msg").substring(11);
        QlCookie qlCookie = JSONObject.parseObject(m, QlCookie.class);
        int i = qlCookieService.saveQlCookie(qlCookie);
        if(i > 0){
            msg.append("添加成功！面板与账号映射的id为：" + qlCookie.getId());
        }else{
            msg.append("添加失败！请查看日志！");
        }
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", msg.toString());
        }});
        param.put("message", message);
        return param;
    }

    public JSONObject updateQlCookieStr(JSONObject param){
        JSONObject ql = this.findQl(param);
        com.alibaba.fastjson2.JSONArray message = ql.getJSONArray("message");
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", "请按下面模板输入要更新的面板id:\r\nupdateQlCookie:xxxx");
        }});
        ql.put("message", message);
        Command command = CommandUtils.getDefaultCommand("请输入要更新的面板id:");
        command.setOption("updateQlCookie");
        command.setAction("updateQlCookie");
        command.putParam("from", param.getString("to")).putParam("to", param.getString("to"));
        CommandUtils.addCommand(param.getString("to"), command);
        return ql;
    }

    public JSONObject updateQlCookie(JSONObject param){
        JSONArray message = new JSONArray();
        StringBuilder msg = new StringBuilder();
        String m = param.getString("msg").substring(15);
        List<Ql> qls = qlService.getQl(new HashMap<>(1){{put("id", m);}});
        if(qls != null){
            for (Ql ql : qls) {
                JSONObject qlToken = CommandUtils.getQlToken(ql.getAddress() + "/open/auth/token?client_id=" + ql.getClientId() + "&client_secret=" + ql.getClientSecret());
                if(qlToken == null){
                    msg.append("配置有误,无法获取登录凭证!");
                }else{
                    ql.setTokeType(qlToken.getString("token_type"));
                    ql.setToken(qlToken.getString("token"));
                    int i = qlService.saveQl(ql);
                    if(i > 0){
                        msg.append("更新成功！青龙面板的名称为：" + ql.getName());
                    }else{
                        msg.append("添加失败！请查看日志！");
                    }
                }
            }
        }
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", msg.toString());
        }});
        param.put("message", message);
        return param;
    }

    /**
     * 设置ck过期时间
     * @param param 参数
     * @return json
     */
    public JSONObject setCookieExpStr(JSONObject param){
        JSONArray message = new JSONArray();
        StringBuilder msg = new StringBuilder();
        msg.append("请按下面模板完善json:\r\n");
        msg.append("setCookieExpDate{\"id\":\"1556554834355539970\",\"from\":\"\",\"to\":\"952219232\",\"ck\":\"\",\"month\":\"\"}");
        Command command = CommandUtils.getDefaultCommand(msg.toString());
        command.setOption("setCookieExpDate");
        command.setAction("setCookieExpDate");
        command.putParam("from", param.getString("to")).putParam("to", param.getString("to"));
        CommandUtils.addCommand(param.getString("to"), command);
        message.add(new JSONObject(){{
            put("to", param.getString("to"));
            put("msg", msg.toString());
        }});
        param.put("message", message);
        return param;
    }

    /**
     * 设置ck过期时间
     * @param param 参数
     * @return json
     */
    public JSONObject setCookieExpDate(JSONObject param){
        String m = param.getString("msg").substring(16);
        JSONObject obj = JSONObject.parseObject(m);
        if(obj.getIntValue("month") == 12){
            return this.setCookieExpForYear(obj);
        }else{
            return this.setCookieExpForMonth(obj);
        }
    }

}
