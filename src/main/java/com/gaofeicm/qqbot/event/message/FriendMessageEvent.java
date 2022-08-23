package com.gaofeicm.qqbot.event.message;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.gaofeicm.qqbot.command.CommandService;
import com.gaofeicm.qqbot.command.entity.Command;
import com.gaofeicm.qqbot.entity.Cookie;
import com.gaofeicm.qqbot.service.CookieService;
import com.gaofeicm.qqbot.service.JdServiceImpl;
import com.gaofeicm.qqbot.service.QlService;
import com.gaofeicm.qqbot.utils.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Gaofeicm
 */
@Component
public class FriendMessageEvent extends MessageEvent implements ApplicationRunner {

    private final static Logger log = LoggerFactory.getLogger(FriendMessageEvent.class);

    @Resource
    private CookieService cookieService;

    @Resource
    private CommandService commandService;

    @Resource
    private QlService qlService;

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
        String msg = message.getString("message").trim();
        String qq = message.getString("user_id");
        log.info("收到QQ：" + qq + "的消息，内容为:" + msg);
        //命令检查
        List<Command> cmds = CommandUtils.getCommand(qq);
        if(cmds != null && !cmds.isEmpty()){
            Command cmd = cmds.get(0);
            cmd.putParam("msg", msg);
            int index = CommonUtils.indexOf(msg, cmd.getOption());
            if(index > -1){
                JSONObject obj = (JSONObject) SpringUtils.invokeMethod(SpringUtils.getBean(cmd.getBean()), cmd.getAction().get(index).toString(), new Class[]{JSONObject.class}, cmd.getParam());
                this.executeReturnMessage(obj);
                cmds.remove(cmd);
                return;
            }else{
                if("取消当前待办".equals(msg)){
                    cmds.remove(cmd);
                    MessageUtils.sendPrivateMsg(qq, "任务已取消！");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("指令回复出错!您当前的待办任务如下：\r\n");
                sb.append(cmd.getMessage());
                MessageUtils.sendPrivateMsg(qq, sb.toString());
                return;
            }
        }
        //ck检查
        String ck = jdService.checkCk(msg);
        if(ck != null){
            MessageUtils.sendPrivateMsg(qq, "正在检查账号状态，请稍候。。。");
            String loadMsg = jdService.loadCk(ck, qq);
            MessageUtils.sendPrivateMsg(qq, loadMsg);
            return;
        }
        //按QQ查询信息
        if(msg.startsWith("查询QQ：")){
            if(!qq.equals(CommonUtils.getAdminQq())){
                return;
            }
            String tq = msg.substring(5);
            List<Cookie> cookies = cookieService.getCookieByQq(tq);
            StringBuilder m = new StringBuilder("QQ").append(tq).append("共有").append(cookies.size()).append("个账号").append("\r\n");
            if(cookies.size() > 0){
                m.append("id  QQ                  pin                              启用    到期时间\r\n");
                for (int i = 0; i < cookies.size(); i++) {
                    Cookie cookie = cookies.get(i);
                    m.append(cookie.getId()).append("      ").append(cookie.getQq()).append("    ").append(cookie.getPtPin(), 7, cookie.getPtPin().length() - 1).append("       ").append((boolean)cookie.getAvailable() ? "是" : "否").append("  ").append(SDF.format(cookie.getExpirationTime())).append("\r\n");
                }
            }
            MessageUtils.sendPrivateMsg(qq, m.toString());
            return;
        }
        switch (msg){
            case "查询":
                this.findCkAccount(qq);
                break;
            case "菜单":
                MessageUtils.sendPrivateMsg(qq, this.getMenuStr());
                break;
            case "同步":
                this.syncQlCookie(qq, msg);
                break;
            case "设置过期时间":
                this.executeReturnMessage(commandService.setCookieExpStr(new JSONObject(){{put("to", qq);}}));
                break;
            case "分配面板":
                this.executeReturnMessage(commandService.addQlCookieStr(new JSONObject(){{put("to", qq);}}));
                break;
            case "检查":
                jdService.cronCheckCkLogin();
                break;
            case "启动检查":
                JdServiceImpl.checkCkSwitch = true;
                break;
            case "关闭检查":
                JdServiceImpl.checkCkSwitch = false;
                break;
            case "原理":
                MessageUtils.sendPrivateMsg(qq, "1、自动帮忙做一些需要手动点的任务\r\n 2、一些活动需要邀请好友帮忙做任务，大家挂一起都是互相互助\r\n");
                break;
            case "教程":
                MessageUtils.sendPrivateMsg(qq, this.getJcStr());
                break;
            case "账号管理":
                this.manageAccount(qq, msg);
                break;
            case "面板管理":
                this.manageQl(qq, msg);
                break;
            case "小姐姐":
                MessageUtils.sendPrivateMsg(qq, "[CQ:image,file=http://api.btstu.cn/sjbz/zsy.php,cache=0]");
                break;
            case "舔狗":
            case "舔狗日记":
                MessageUtils.sendPrivateMsg(qq, HttpRequestUtils.doGet("https://api.oick.cn/dog/api.php"));
                break;
            case "二次元":
                MessageUtils.sendPrivateMsg(qq, "[CQ:image,file=http://www.dmoe.cc/random.php,cache=0]");
                break;
            case "打赏":
                MessageUtils.sendPrivateMsg(qq, "[CQ:image,file=https://raw.githubusercontent.com/gaofeicm/gaofeicm.github.io/master/assets/images/donate.png,cache=1]");
                break;
            case "新闻":
                MessageUtils.sendPrivateMsg(qq, "今日新闻：\r\n" + JSONObject.parseObject(HttpRequestUtils.doGet("http://bjb.yunwj.top/php/qq.php")).getString("wb").replaceFirst("【换行】", "").replaceAll("【换行】", "\r\n"));
                break;
            default:
                MessageUtils.sendPrivateMsg(qq, "我只是个笨蛋机器人，你说的话我还不会接。不要连续发我听不懂的话，发多了我不理你了！");
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
        StringBuilder message = new StringBuilder("----------------------------面板管理----------------------\r\n");
        message.append("1、添加面板\r\n");
        message.append("2、查看面板\r\n");
        message.append("3、删除面板\r\n");
        Command command = CommandUtils.getDefaultCommand(message.toString());
        command.setOption("1", "2", "3");
        command.setAction("addQlStr", "findQl", "deleteQlStr");
        command.putParam("from", qq).putParam("to", qq);
        CommandUtils.addCommand(qq, command);
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
        StringBuilder message = new StringBuilder("----------------------------账号管理----------------------\r\n");
        message.append("1、用户列表\r\n");
        message.append("2、分配面板\r\n");
        message.append("3、设置过期时间\r\n");
        Command command = CommandUtils.getDefaultCommand(message.toString());
        command.setOption("1", "2", "3");
        command.setAction("findCookie", "addQlCookieStr", "setCookieExpStr");
        command.putParam("from", qq).putParam("to", qq);
        CommandUtils.addCommand(qq, command);
        MessageUtils.sendPrivateMsg(qq, message.toString());
    }

    private void syncQlCookie(String qq, String mag){
        if(!qq.equals(CommonUtils.getAdminQq())){
            return;
        }
        List<Map<String, Object>> cks = cookieService.getAvailableCookie();
        Map<String, Map<String, Object>> qls = new HashMap<>();
        Map<String, JSONArray> tck = new HashMap<>();
        for (Map<String, Object> ck : cks) {
            String name = ck.get("name").toString();
            if(tck.get(name) == null){
                tck.put(name, new JSONArray());
            }
            if(qls.get(name) == null){
                qls.put(name, new HashMap<>(){{
                    put("name", ck.get("name"));
                    put("address", ck.get("address"));
                    put("token", ck.get("token"));
                    put("tokenType", ck.get("tokenType"));
                }});
            }
            tck.get(name).add(new HashMap<>(3){{
                put("value", ck.get("cookie"));
                put("name", "JD_COOKIE");
                put("remarks", ck.get("qq"));
            }});
        }
        StringBuilder sb = new StringBuilder();
        tck.forEach((s, list) -> {
            this.deleteQlOriginalData(qls.get(s));
            this.addQlOriginalData(qls.get(s), list);
            sb.append("面板名称：").append(qls.get(s).get("name")).append("，拥有").append(list.size()).append("条ck\r\n");
        });
        MessageUtils.sendPrivateMsg(qq, "同步完成\r\n" + sb.toString());
    }

    /**
     * 添加面板原始数据
     * @param map map
     * @param list list
     */
    private void addQlOriginalData(Map<String, Object> map, JSONArray list){
        try{
            Map<String, String> header = this.getQlHeader(map);
            String body = list.toJSONString();
            String s = HttpRequestUtils.doPost(map.get("address") + "/open/envs?t=" + System.currentTimeMillis(), body, header);
            if (s != null) {
                JSONObject jsonObject = JSONObject.parseObject(s);
                if (!"200".equals(jsonObject.getString("code"))) {
                    throw new RuntimeException("code为" + jsonObject.getString("code"));
                }
            }else{
                throw new RuntimeException("添加面板原始数据返回值为null");
            }
        }catch (Exception e){
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "添加原始数据出现了异常！" + map.toString() + e.getMessage());
            throw e;
        }
    }

    /**
     * 删除面板原始数据
     * @param map map
     */
    private void deleteQlOriginalData(Map<String, Object> map){
        try{
            JSONArray qlData = this.getQlData(map);
            JSONArray ids = new JSONArray();
            for (Object data : qlData) {
                JSONObject jsonObject = JSONObject.parseObject(data.toString());
                ids.add(jsonObject.getString("id"));
            }
            if(!ids.isEmpty()) {
                Map<String, String> header = this.getQlHeader(map);
                String s = HttpRequestUtils.doDelete(map.get("address") + "/open/envs?&t=" + System.currentTimeMillis(), ids.toJSONString(), header);
                if (s != null) {
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    if (!"200".equals(jsonObject.getString("code"))) {
                        throw new RuntimeException();
                    }
                }else{
                    throw new RuntimeException("删除面板原始数据返回值为null");
                }
            }
        }catch (Exception e){
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "删除原始数据出现了异常！" + map.toString() + e.getMessage());
            throw e;
        }
    }

    /**
     * 获取青龙数据
     * @param map map
     * @return array
     */
    private JSONArray getQlData(Map<String, Object> map){
        Map<String, String> header = this.getQlHeader(map);
        String s = HttpRequestUtils.doGet(map.get("address") + "/open/envs?searchValue=&t=" + System.currentTimeMillis(), header);
        if(s != null){
            return JSONObject.parseObject(s).getJSONArray("data");
        }else{
            MessageUtils.sendPrivateMsg(CommonUtils.getAdminQq(), "获取青龙面板数据出现了异常！" + map.get("address"));
            throw new RuntimeException();
        }
    }

    /**
     * 获取用户信息
     * @param qq qq
     */
    private void findCkAccount(String qq){
        List<Cookie> cookies = cookieService.getCookieByQq(qq);
        int count = cookies.size();
        if(count == 0){
            MessageUtils.sendPrivateMsg(qq, "您还没有添加账号，请根据群公告获取ck后发给我");
            return;
        }
        long t = System.currentTimeMillis();
        List<Cookie> availableCookies = cookies.stream().filter(cookie -> (boolean) cookie.getAvailable() && cookie.getExpirationTime().getTime() > t).collect(Collectors.toList());
        long count1 = availableCookies.size();
        long count2 = cookies.stream().filter(cookie -> !(boolean) cookie.getAvailable() || cookie.getExpirationTime().getTime() <= t).count();
        StringBuilder m = new StringBuilder("执行查询命令，您总共有").append(cookies.size()).append("个账号");
        if(count1 > 0 && count2 > 0){
            m.append("，").append(count1).append("个有效账号，").append(count2).append("个失效账号！\r\n");
            m.append("可能存在的情况：\r\n1、代挂服务未过期，ck过期了，请重新提交，并联系管理员刷新ck\r\n2、代挂服务过期了，请续费后重新获取ck发送给我，并联系管理员审核\r\n\r\n");
        }
        if(count1 == 0){
            m.append("，").append(count2).append("个失效账号！\r\n");
        }
        MessageUtils.sendPrivateMsg(qq, m.toString());
        if(count1 > 0){
            availableCookies.forEach(cookie -> {
                String ckAccount = this.findCkAccountInfo(cookie.getCookie());
                MessageUtils.sendPrivateMsg(qq, ckAccount);
            });
        }
    }

    /**
     * 查询账户信息
     * @param  ck ck
     * @return 查询结果
     */
    private String findCkAccountInfo(String ck){
        JSONObject userInfo1 = jdService.getUserInfo1(ck);
        if(userInfo1 != null && "1001".equals(userInfo1.getString("retcode"))){
            return "您的ck:[" + ck + "]已过期！";
        }
        JSONObject userInfo2 = jdService.getUserInfo2(ck);
        Map<String, Object> bean = jdService.getBean(ck);
        JSONObject jxmc = jdService.getJxmc(ck);
        JSONObject jdjs = jdService.getJdjs(ck);
        JSONObject jdzz = jdService.getJdzz(ck);
        JSONObject jdms = jdService.getJdms(ck);
        JSONObject jdww = jdService.getJdww(ck);
        JSONObject jdnc = jdService.getJdnc(ck);
        StringBuffer msg = new StringBuffer("【账号\uD83C\uDD94】");
        String nickName = "";
        String levelName = "";
        String userLevel = "";
        String isPlusVip = "";
        int beanCount = 0;
        String couponNum = "";
        String jingXiang = "";
        if(userInfo1 != null && "0".equals(userInfo1.getString("retcode"))){
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
        if(jdjs != null && "0".equals(jdjs.getString("code"))){
            int v = jdjs.getJSONObject("data").getIntValue("goldBalance");
            msg.append("【极速金币】").append(v).append("币(≈").append(new DecimalFormat("0.00").format((float)v / 10000)).append("元)\r\n");
        }
        if(jdzz != null && "0".equals(jdzz.getString("code"))){
            int v = jdzz.getJSONObject("data").getIntValue("totalNum");
            msg.append("【京东赚赚】").append(v).append("币(≈").append(new DecimalFormat("0.00").format((float)v / 10000)).append("元)\r\n");
        }
        if(jdms != null && "2041".equals(jdms.getString("code")) || "2042".equals(jdms.getString("code"))){
            int v = jdms.getJSONObject("result").getJSONObject("assignment").getIntValue("assignmentPoints ");
            msg.append("【京东秒杀】").append(v).append("币(≈").append(new DecimalFormat("0.00").format((float)v / 1000)).append("元)\r\n");
        }
        if(jxmc != null && "0".equals(jxmc.getString("ret"))){
            int v = jxmc.getJSONObject("data").getIntValue("eggcnt");
            if(v == 0){
                msg.append("【京喜牧场】未开通或提示火爆\n");
            }else{

                msg.append("【京喜牧场】").append(v).append("枚鸡蛋\r\n");
            }
        }
        if(jdww != null && jdww.getBooleanValue("success")){
            int v = jdww.getIntValue("level");
            msg.append("【汪汪乐园】汪汪等级:").append(v).append("级\r\n");
        }else{
            String v = jdww.getString("errMsg");
            msg.append("【汪汪乐园】").append(v).append("\r\n");
        }
        if(jdnc != null && jdnc.getString("JdFarmProdName") != null){
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

    /**
     * 获取菜单回复文字
     * @return 教程
     */
    private String getMenuStr(){
        StringBuilder sb = new StringBuilder();
        sb.append("------------京 东 区------------\r\n");
        sb.append("查询   原理    教程    签到（未上线）\r\n");
        sb.append("------------娱 乐 区------------\r\n");
        sb.append("打赏 | 你的鼓励是我最大的动力\r\n");
        sb.append("小姐姐 | 看小姐姐\r\n");
        sb.append("二次元 | 看二次元小姐姐\r\n");
        sb.append("舔狗 | 获取舔狗日记\r\n");
        sb.append("舔狗日记 | 获取舔狗日记\r\n");
        sb.append("新闻 | 获取时事新闻\r\n");
        return sb.toString();
    }

    public Map<String, String> getQlHeader(Map<String, Object> map){
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", map.get("tokenType") + " " + map.get("token"));
        header.put("User-Agent", "qqbot client");
        header.put("content-type", "application/json");
        return header;
    }

    /**
     * 处理命令返回待发送的信息
     * @param obj 信息集合
     */
    private void executeReturnMessage(JSONObject obj){
        JSONArray messageList = obj.getJSONArray("message");
        messageList.forEach(me -> {
            JSONObject m = (JSONObject) me;
            MessageUtils.sendPrivateMsg(m.getString("to"), m.getString("msg"));
        });
    }

}
