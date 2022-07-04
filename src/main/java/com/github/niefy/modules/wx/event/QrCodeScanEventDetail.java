package com.github.niefy.modules.wx.event;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.niefy.common.utils.DateUtils;
import com.github.niefy.common.utils.WxMpHtmlPageApiUtil;
import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.service.WxAccountService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class QrCodeScanEventDetail implements ApplicationListener<QrCodeScanChangeEvent<? extends QrCodeScanState>> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxAccountService wxAccountService;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @SneakyThrows
    @Override
    public void onApplicationEvent(QrCodeScanChangeEvent<? extends QrCodeScanState> event) {
        WxAccount wxAccount = (WxAccount) event.getSource();
        String appid = wxAccount.getAppid();
        QrCodeScanState state = event.getPayload();
        String accountName = wxAccount.getName();
        switch (state) {
            case WAIT_SCAN:
                // 网页二维码，还没有被扫描。
            case SCAN:
                // 网页二维码，已被扫描，等待用户确认，
                // 休息一下，继续检测。
                ThreadUtil.sleep(2000);
                QrCodeScanState qrCodeScanState = WxMpHtmlPageApiUtil.scanloginqrcodeAck(wxAccount.getHtmlPageCookies());
                if (!qrCodeScanState.equals(QrCodeScanState.WAIT_SCAN)) {
                    QrCodeScanState.clearQrcodeInfoOf(appid);
                }
                QrCodeScanChangeEvent.publish(wxAccount, qrCodeScanState);
                break;
            case EXPIRE:
                logger.info("[{}]的网页二维码，已过期", accountName);
                break;
            case DONE:
                logger.info("[{}]的网页二维码，已被扫描，用户已确认。准备获取token", accountName);
                JSONObject result = WxMpHtmlPageApiUtil.login(wxAccount.getHtmlPageCookies());
                String cookies = result.getString("cookies");
                wxAccount = wxAccountService.getById(appid);
                wxAccount.setHtmlPageCookies(cookies);
                wxAccount.setHtmlPageToken(result.getString("token"));
                Date date = new Date();
                wxAccount.setHtmlPageCookiesSyncTime(date);
                wxAccount.setHtmlPageCookiesExpireTime(DateUtils.addDateDays(date, 30));
                wxAccountService.updateById(wxAccount);
                break;
            default:
                QrCodeScanState.clearQrcodeInfoOf(appid);
                logger.info("[{}]的网页二维码，检测到未知扫描状态，中止后续操作。", accountName);
        }
    }


}
