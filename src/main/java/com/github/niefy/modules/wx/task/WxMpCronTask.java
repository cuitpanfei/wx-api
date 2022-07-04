package com.github.niefy.modules.wx.task;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.service.WxAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WxMpCronTask {

    @Autowired
    WxAccountService wxAccountService;

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
            } catch (Throwable e) {
                log.error("try sync {} fail", account.getAppid(), e);
            }
        });

    }

}