package com.github.niefy.modules.wx.task;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.niefy.modules.wx.entity.MsgReplyRule;
import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.service.MsgReplyRuleService;
import com.github.niefy.modules.wx.service.WxAccountService;
import lombok.extern.slf4j.Slf4j;
import nonapi.io.github.classgraph.json.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WxMpCronTask {

    @Value("${ksj.token}")
    String token = "";
    @Autowired
    WxAccountService wxAccountService;

    @Autowired
    MsgReplyRuleService replyRuleService;

    @Scheduled(cron = "10 */5 * * * ?")
    public void execute() {
        long randomLong = RandomUtil.randomLong(1000, 90000);
        log.info("{}ms 后执行任务", randomLong);
        ThreadUtil.sleep(randomLong);
        log.info("check live start");
        List<WxAccount> acounts = keepLive();
        log.info("check live end. try sync");
        sync(acounts);
        log.info("sync end");
    }


    public void syncKsj() {
        log.info("ksy sale sync start.");
        HttpRequest saleListReq = HttpUtil.createGet("https://www.ksjhaoka.com/api/admin/store/sale?status=1&current=1&size=50&sort=%2Bid");
        saleListReq.header(Header.AUTHORIZATION, "bearer " + token)
                .header(Header.COOKIE, "vue_admin_template_token=" + token);
        HttpResponse response = saleListReq.execute();
        if (response.isOk()) {
            String result = response.body();
            JSONArray saleList = JSON.parseObject(result).getJSONObject("data").getJSONArray("list");
            replyRuleService.lambdaUpdate()
                    .eq(MsgReplyRule::getDesc, "ksj")
                    .set(MsgReplyRule::isStatus,Boolean.FALSE).update();
            List<MsgReplyRule> replyRules = replyRuleService.lambdaQuery().eq(MsgReplyRule::getDesc, "ksj").list();
            Map<String, MsgReplyRule> map = replyRules.stream().collect(Collectors.toMap(MsgReplyRule::getRuleName, Function.identity()));
            List<MsgReplyRule> list = new ArrayList<>();
            for (Object sale : saleList) {
                JSONObject saleData = ((JSONObject) sale);
                String name = saleData.getString("name");
                MsgReplyRule rule = map.computeIfAbsent(name, key -> new MsgReplyRule());
                //归属地
                String packageAttribution = saleData.getString("package_attribution");
                //不发货地区
                String forbiddenArea = saleData.getString("forbidden_area");
                //备注
                String remark = saleData.getString("remark");
                //配送方式
                String delivery = saleData.getString("delivery");
                //激活方式
                String activateType = saleData.getString("activate_type");
                //首充渠道
                String initialChargeChannel = saleData.getString("initial_charge_channel");
                //办理年龄
                String ageLimit = saleData.getString("age_limit");
                //违停复机
                String backstopMachine = saleData.getString("backstop_machine");
                //套餐合约
                String packageContractText = saleData.getString("package_contract_text");
                //套餐构成
                String packageComposition = saleData.getString("package_composition");
                rule.setRuleName(name);
                rule.setAppid("");
                rule.setExactMatch(false);
                rule.setStatus(true);
                rule.setSync(true);
                rule.setDesc("ksj");
                rule.setReplyType("text");
                rule.setMatchValue(name + "," + name.substring(0, 2));
                rule.setReplyContent(String.format("%s\n\n归属地：%s\n不发货地区：%s\n" +
                                "备注：%s\n配送方式：%s\n激活方式：%s\n首充渠道：%s\n" +
                                "办理年龄：%s\n违停复机：%s\n套餐合约：%s\n套餐构成：%s",
                        name, packageAttribution, forbiddenArea, remark,
                        delivery, activateType, initialChargeChannel, ageLimit,
                        backstopMachine, packageContractText, packageComposition));
                list.add(rule);
            }
            MsgReplyRule carsInfo;
            try {
                carsInfo = replyRuleService.getOne(replyRuleService.lambdaQuery().eq(MsgReplyRule::getRuleName, "电话卡"), false);
            } catch (Throwable e) {
                log.warn("没有主推广信息，将要生成");
                carsInfo = new MsgReplyRule();
            }
            if (list.size() > 0) {
                Optional.ofNullable(carsInfo)
                        .map(r -> {
                            r.setRuleId(42L);
                            r.setAppid("");
                            r.setMatchValue("电话卡");
                            r.setExactMatch(false);
                            r.setStatus(true);
                            r.setSync(true);
                            r.setDesc("ksj");
                            r.setRuleName("电话卡");
                            r.setReplyType("text");
                            r.setReplyContent(list.stream().map(MsgReplyRule::getRuleName).collect(Collectors.joining("\n")));
                            return r;
                        }).ifPresent(r -> replyRuleService.saveOrUpdate(r));
            }
            replyRuleService.saveOrUpdateBatch(list);
        }
        log.info("ksy sale sync end.");
    }

    private List<WxAccount> keepLive() {
        List<WxAccount> list = wxAccountService.list();
        wxAccountService.checkAndUpdate(list);
        return list.stream()
                .filter(wxAccount -> wxAccount.getHtmlPageCookiesExpireTime().after(new Date()))
                .collect(Collectors.toList());
    }

    private void sync(List<WxAccount> accounts) {
        accounts.forEach(account -> {
            try {
                wxAccountService.sync(account.getAppid());
                ThreadUtil.execAsync(this::syncKsj);
            } catch (Throwable e) {
                log.error("try sync {} fail", account.getAppid(), e);
            }
        });

    }

}