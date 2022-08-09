package com.gaofeicm.qqbot.event.message;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.command.entity.Command;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.service.CookieServiceImpl;
import com.gaofeicm.qqbot.service.JdServiceImpl;
import com.gaofeicm.qqbot.utils.*;
import lombok.SneakyThrows;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gaofeicm
 */
@Component
public class FriendMessageEvent extends MessageEvent implements ApplicationRunner {

    @Resource
    private CookieServiceImpl cookieService;

    @Resource
    private JdServiceImpl jdService;

    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getEventName() {
        return "friend";
    }

    @SneakyThrows
    @Override
    public void handle(JSONObject message) {
        String msg = message.getString("message");
        String qq = message.getString("user_id");
        //命令检查
        List<Command> cmds = CommandUtils.getCommand(qq);
        if(cmds != null && !cmds.isEmpty()){
            Command cmd = cmds.get(0);
            int index = CommonUtils.indexOf(msg, cmd.getOption());
            if(index > -1){
                JSONObject obj = (JSONObject) SpringUtils.invokeMethod(SpringUtils.getBean(cmd.getBean()), cmd.getAction().get(index).toString(), new Class[]{JSONObject.class}, cmd.getParam());
                JSONArray messageList = obj.getJSONArray("message");
                messageList.forEach(me -> {
                    JSONObject m = (JSONObject) me;
                    MessageUtils.sendPrivateMsg(m.getString("to"), m.getString("msg"));
                });
                cmds.remove(cmd);
                return;
            }
        }
        //ck检查
        String ck = jdService.checkCk(msg);
        if(ck != null){
            MessageUtils.sendPrivateMsg(message.getString("user_id"), "正在检查账号状态，请稍候。。。");
            String loadMsg = jdService.loadCk(ck, qq);
            MessageUtils.sendPrivateMsg(message.getString("user_id"), loadMsg);
            return;
        }
        switch (msg){
            case "查询":
                List<Cookie> cookies = cookieService.getCookieByQq(qq);
                cookies.forEach(cookie -> {
                    String ckAccount = this.findCkAccount(cookie.getCookie());
                    MessageUtils.sendPrivateMsg(qq, ckAccount);
                });
                break;
            case "原理":
                MessageUtils.sendPrivateMsg(message.getString("user_id"), "1、自动帮忙做一些需要手动点的任务\r\n 2、一些活动需要邀请好友帮忙做任务，大家挂一起都是互相互助\r\n");
                break;
            case "教程":
                MessageUtils.sendPrivateMsg(message.getString("user_id"), this.getJcStr());
                break;
            case "账号管理":
                this.manageAccount(qq, msg);
                break;
            case "面板管理":
                this.manageQl(qq, msg);
                break;
            case "小姐姐":
                MessageUtils.sendPrivateMsg(message.getString("user_id"), "[CQ:image,file=http://api.btstu.cn/sjbz/zsy.php,cache=0]");
                break;
            case "舔狗":
            case "舔狗日记":
                MessageUtils.sendPrivateMsg(message.getString("user_id"), HttpRequestUtils.doGet("https://api.oick.cn/dog/api.php"));
                break;
            case "二次元":
                MessageUtils.sendPrivateMsg(message.getString("user_id"), "[CQ:image,file=http://www.dmoe.cc/random.php,cache=0]");
                break;
            case "新闻":
                MessageUtils.sendPrivateMsg(message.getString("user_id"), "今日新闻（" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "）：\r\n" + JSONObject.parseObject(HttpRequestUtils.doGet("http://bjb.yunwj.top/php/qq.php")).getString("wb").replaceFirst("【换行】", "").replaceAll("【换行】", "\r\n"));
                break;
            default:
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        super.register(this);
    }

    /**
     * ck管理
     * @param qq qq
     * @param mag 消息
     */
    private void manageQl(String qq, String mag){
        if(!qq.equals(CommonUtils.getAdminQq())){
            return;
        }
        List<Cookie> cookies = cookieService.getCookie(new HashMap<>(0));
        StringBuilder message = new StringBuilder("----------------------------cookie列表----------------------\r\n");
        message.append("序号  QQ                  pin                              启用    到期时间\r\n");
        /*Command command = CommandUtils.getDefaultCommand(message);
        command.setOption("1", "2", "3");
        command.setAction("setCookieExpForMonth", "setCookieExpForYear", "refuseCookie");
        command.putParam("from", qq);*/
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            message.append(i + 1).append("      ").append(cookie.getQq()).append("    ").append(cookie.getPtPin(), 7, cookie.getPtPin().length() - 1).append("       ").append((boolean)cookie.getAvailable() ? "是" : "否").append("  ").append(SDF.format(cookie.getExpirationTime())).append("\r\n");
        }
        MessageUtils.sendPrivateMsg(qq, message.toString());
    }

    /**
     * ck管理
     * @param qq qq
     * @param mag 消息
     */
    private void manageAccount(String qq, String mag){
        if(!qq.equals(CommonUtils.getAdminQq())){
            return;
        }
        List<Cookie> cookies = cookieService.getCookie(new HashMap<>(0));
        StringBuilder message = new StringBuilder("----------------------------cookie列表----------------------\r\n");
        message.append("序号  QQ                  pin                              启用    到期时间\r\n");
        /*Command command = CommandUtils.getDefaultCommand(message);
        command.setOption("1", "2", "3");
        command.setAction("setCookieExpForMonth", "setCookieExpForYear", "refuseCookie");
        command.putParam("from", qq);*/
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            message.append(i + 1).append("      ").append(cookie.getQq()).append("    ").append(cookie.getPtPin(), 7, cookie.getPtPin().length() - 1).append("       ").append((boolean)cookie.getAvailable() ? "是" : "否").append("  ").append(SDF.format(cookie.getExpirationTime())).append("\r\n");
        }
        MessageUtils.sendPrivateMsg(qq, message.toString());
    }

    /**
     * 查询账户信息
     * @param  ck ck
     * @return 查询结果
     */
    private String findCkAccount(String ck){
        JSONObject userInfo1 = jdService.getUserInfo1(ck);
        JSONObject userInfo2 = jdService.getUserInfo2(ck);
        Map<String, Object> bean = jdService.getBean(ck);
        JSONObject jxmc = jdService.getJxmc(ck);
        JSONObject jdjs = jdService.getJdjs(ck);
        JSONObject jdzz = jdService.getJdzz(ck);
        JSONObject jdms = jdService.getJdms(ck);
        JSONObject jdww = jdService.getJdww(ck);
        JSONObject jdnc = jdService.getJdnc(ck);
        if("1001".equals(userInfo1.getString("retcode"))){
            return "您的ck:[" + ck + "]已过期！";
        }
        StringBuffer msg = new StringBuffer("【账号\uD83C\uDD94】");
        String nickName = "";
        String levelName = "";
        String userLevel = "";
        String isPlusVip = "";
        int beanCount = 0;
        String couponNum = "";
        String jingXiang = "";
        if("0".equals(userInfo1.getString("retcode"))){
            JSONObject data = userInfo1.getJSONObject("data");
            if(data != null && data.containsKey("userInfo")){
                JSONObject userInfo = data.getJSONObject("userInfo");
                JSONObject baseInfo = userInfo.getJSONObject("baseInfo");
                nickName = baseInfo.getString("nickname");
                levelName = baseInfo.getString("levelName");
                userLevel = baseInfo.getString("userLevel");
                isPlusVip = baseInfo.getString("isPlusVip");
            }
            if(data != null && data.containsKey("assetInfo")) {
                JSONObject assetInfo = data.getJSONObject("assetInfo");
                beanCount = assetInfo.getIntValue("beanNum");
                couponNum = assetInfo.getString("couponNum");
            }
        }
        if(userInfo2.getJSONObject("user") != null){
            jingXiang = userInfo2.getJSONObject("user").getString("uclass");
        }
        msg.append(nickName).append("\r\n");
        msg.append("【账号信息】").append(jdService.getAccountInfoStr(levelName, isPlusVip, userLevel, jingXiang)).append("\r\n");
        msg.append("【今日京豆】").append(bean.get("today")).append("\r\n");
        msg.append("【昨日京豆】").append(bean.get("yesterday")).append("\r\n");
        msg.append("【当前京豆】").append(beanCount).append("豆(≈").append(new DecimalFormat("0.00").format((float)beanCount / 100)).append("元)\r\n");
        msg.append("【优惠券】").append(couponNum).append("张\r\n");
        if("0".equals(jxmc.getString("ret"))){
            int v = jxmc.getJSONObject("data").getIntValue("eggcnt");
            msg.append("【京喜牧场】").append(v).append("枚鸡蛋\r\n");
        }
        if("0".equals(jdjs.getString("code"))){
            int v = jdjs.getJSONObject("data").getIntValue("goldBalance");
            msg.append("【极速金币】").append(v).append("币(≈").append(new DecimalFormat("0.00").format((float)v / 10000)).append("元)\r\n");
        }
        if("0".equals(jdzz.getString("code"))){
            int v = jdzz.getJSONObject("data").getIntValue("totalNum");
            msg.append("【京东赚赚】").append(v).append("币(≈").append(new DecimalFormat("0.00").format((float)v / 10000)).append("元)\r\n");
        }
        if("2041".equals(jdms.getString("code")) || "2042".equals(jdms.getString("code"))){
            int v = jdms.getJSONObject("result").getJSONObject("assignment").getIntValue("assignmentPoints ");
            msg.append("【京东秒杀】").append(v).append("币(≈").append(new DecimalFormat("0.00").format((float)v / 1000)).append("元)\r\n");
        }
        if("0".equals(jxmc.getString("ret"))){
            int v = jxmc.getJSONObject("data").getIntValue("eggcnt");
            if(v == 0){
                msg.append("【京喜牧场】未开通或提示火爆\n");
            }else{

                msg.append("【京喜牧场】").append(v).append("枚鸡蛋\r\n");
            }
        }
        if(jdww.getBooleanValue("success")){
            int v = jdww.getIntValue("level");
            msg.append("【汪汪乐园】汪汪等级:").append(v).append("级\r\n");
        }else{
            String v = jdww.getString("errMsg");
            msg.append("【汪汪乐园】").append(v).append("\r\n");
        }
        if(jdnc.getString("JdFarmProdName") != null){
            msg.append("【东东农场】");
            int treeState = jdnc.getIntValue("treeState");
            if(jdnc.getIntValue("JdtreeEnergy") != 0){
                if(treeState == 2 || treeState == 3){
                    msg.append(jdnc.getString("JdFarmProdName")).append("可以兑换了!");
                }else{
                    if(!"Infinity".equals(jdnc.getString("JdwaterD")) && !"-Infinity".equals(jdnc.getString("JdwaterD"))){
                        msg.append(jdnc.getString("JdFarmProdName")).append(String.format("%.2f", (jdnc.getDoubleValue("JdtreeEnergy") / jdnc.getDoubleValue("JdtreeTotalEnergy")) * 100)).append("%,").append(jdnc.getDoubleValue("JdwaterD")).append("天\r\n");
                    }else{
                        msg.append(jdnc.getString("JdFarmProdName")).append(String.format("%.2f", (jdnc.getDoubleValue("JdtreeEnergy") / jdnc.getDoubleValue("JdtreeTotalEnergy")) * 100)).append("%\r\n");
                    }
                }
            }else{
                if(treeState == 0){
                    msg.append("水果领取后未重新种植!\r\n");
                }else if (treeState == 1){
                    msg.append("种植中...\r\n");
                }else {
                    msg.append("状态异常!\r\n");
                }
            }
        }
        //msg.append("【京喜工厂】").append(2399).append("枚鸡蛋\r\n");
        //msg.append("【东东萌宠】").append(2399).append("枚鸡蛋\r\n");
        return msg.toString();
    }

    /**
     * 获取教程回复文字
     * @return 教程
     */
    private String getJcStr(){
        StringBuilder sb = new StringBuilder();
        sb.append("发送 查询，查看个人账户全部信息，3次/天\r\n");
        sb.append("发送 月度查询，查询个人账户本月全部收益，1次/天\r\n");
        sb.append("发送 原理，了解是如何领京豆和红包的\r\n");
        sb.append("\r\n");
        sb.append("活动攻略:\r\n");
        sb.append("【东东农场】京东->我的->东东农场,完成是京东红包,可以用于京东app的任意商品\r\n");
        sb.append("【极速金币】京东极速版->我的->金币(极速版使用)\r\n");
        sb.append("【京东赚赚】微信->京东赚赚小程序->底部赚好礼->提现无门槛红包(京东使用)\r\n");
        sb.append("【京东秒杀】京东->中间频道往右划找到京东秒杀->中间点立即签到->兑换无门槛红包(京东使用)\r\n");
        sb.append("【东东萌宠】京东->我的->东东萌宠,完成是京东红包,可以用于京东app的任意商品\r\n");
        sb.append("【领现金】京东->我的->东东萌宠->领现金(微信提现 京东红包)\r\n");
        sb.append("【京喜工厂】京喜->我的->京喜工厂,完成是商品红包,用于购买指定商品(不兑换会过期)\r\n");
        sb.append("【京东金融】京东金融app->我的->养猪猪,完成是白条支付券,支付方式选白条支付时立减.\r\n");
        sb.append("【其他】京喜红包只能在京喜使用,其他同理\r\n");
        sb.append("\r\n");
        sb.append("新号最好别挂，会影响到别人的号的，挂多个号的请重新下单，未重新拍的提交了也不会生效，过期了请及时续费，未续费重新提交的会被系统随机检测到封账号，请及时续费。\r\n");
        return sb.toString();
    }

}
