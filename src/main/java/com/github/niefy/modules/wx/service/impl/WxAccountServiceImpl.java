package com.github.niefy.modules.wx.service.impl;

import cn.hutool.core.util.HexUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Query;
import com.github.niefy.common.utils.WxMpHtmlPageApiUtil;
import com.github.niefy.modules.wx.dao.WxAccountMapper;
import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.event.QrCodeScanChangeEvent;
import com.github.niefy.modules.wx.event.QrCodeScanState;
import com.github.niefy.modules.wx.service.WxAccountService;
import com.github.niefy.modules.wx.task.WxMpCronTask;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("wxAccountService")
public class WxAccountServiceImpl extends ServiceImpl<WxAccountMapper, WxAccount> implements WxAccountService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WxMpService wxMpService;

    @Autowired
    WxMpCronTask task;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String) params.get("name");
        IPage<WxAccount> page = this.page(
                new Query<WxAccount>().getPage(params),
                new QueryWrapper<WxAccount>()
                        .like(StringUtils.hasText(name), "name", name)
        );

        return new PageUtils(page);
    }

    @PostConstruct
    public void loadWxMpConfigStorages() {
        logger.info("加载公众号配置...");
        List<WxAccount> accountList = this.list();
        if (accountList == null || accountList.isEmpty()) {
            logger.info("未读取到公众号配置，请在管理后台添加");
            return;
        }
        logger.info("加载到{}条公众号配置", accountList.size());
        accountList.forEach(this::addAccountToRuntime);
        logger.info("公众号配置加载完成");

        logger.info("同步手机卡推广信息");
        task.syncKsj();
        logger.info("同步手机卡推广信息完成");
    }

    @Override
    public boolean save(WxAccount entity) {
        Assert.notNull(entity, "WxAccount不得为空");
        String appid = entity.getAppid();
        if (this.isAccountInRuntime(appid)) { //已有此appid信息，更新
            logger.info("更新公众号配置");
            wxMpService.removeConfigStorage(appid);
            this.addAccountToRuntime(entity);

            return SqlHelper.retBool(this.baseMapper.updateById(entity));
        } else {//已有此appid信息，新增
            logger.info("新增公众号配置");
            this.addAccountToRuntime(entity);

            return SqlHelper.retBool(this.baseMapper.insert(entity));
        }

    }

    /**
     * 全量同步公众号数据
     *
     * @param appid 公众号应用id
     * @return 同步成功与否的标志
     */
    @Override
    public boolean sync(String appid) {
        log.info("try sync {}",appid);
        WxAccount wxAccount = this.baseMapper.selectById(appid);
        boolean isExpire = WxMpHtmlPageApiUtil.cookieIsExpire(wxAccount);
        if (isExpire) {
            log.info("can't sync {}, it is expired",appid);
            return false;
        }
        //根据token和cookies获取数据
        // 通知中心:          https://mp.weixin.qq.com/cgi-bin/frame?t=notification/index_frame&lang=zh_CN&token=185616876

        // 获取首页信息：      https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=185616876
        // 图文素材：         https://mp.weixin.qq.com/cgi-bin/appmsg?begin=0&count=10&type=10&action=list_card&token=185616876&lang=zh_CN
        // 素材库：
        //    视频素材：      https://mp.weixin.qq.com/cgi-bin/appmsg?begin=0&count=10&type=15&action=list_video&token=185616876&lang=zh_CN
        //    图片素材：      https://mp.weixin.qq.com/cgi-bin/filepage?begin=0&count=12&type=2&token=185616876&lang=zh_CN
        //    音频素材：      https://mp.weixin.qq.com/cgi-bin/filepage?begin=0&count=20&type=3&token=185616876&lang=zh_CN
        // 原创：
        //    图文原创：      https://mp.weixin.qq.com/cgi-bin/appmsgcopyright?action=orignal&type=1&token=185616876&lang=zh_CN
        //    全局可转载帐号:  https://mp.weixin.qq.com/cgi-bin/appmsgcopyright?t=original/g_whitelist&action=global_ori_whitelist&token=185616876&lang=zh_CN
        // 消息：
        //    近期消息:       https://mp.weixin.qq.com/cgi-bin/message?t=message/list&count=20&day=7&token=185616876&lang=zh_CN
        //    已收藏消息:     https://mp.weixin.qq.com/cgi-bin/message?t=message/list&action=star&count=20&token=185616876&lang=zh_CN
        // 用户管理：
        //    已关注：        https://mp.weixin.qq.com/cgi-bin/user_tag?action=get_all_data&lang=zh_CN&token=185616876
        //    黑名单：        https://mp.weixin.qq.com/cgi-bin/user_tag?action=get_black_list&limit=20&offset=0&backfoward=1&token=185616876&lang=zh_CN&f=json&ajax=1&random=0.6363133435077204
        return false;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        Assert.notEmpty(idList, "WxAccount不得为空");

        // 更新wxMpService配置
        logger.info("同步移除公众号配置");
        idList.forEach(id -> wxMpService.removeConfigStorage((String) id));

        return SqlHelper.retBool(this.baseMapper.deleteBatchIds(idList));
    }

    /**
     * 全量同步公众号数据的前置处理，调用微信公众号官网接口，获取登陆二维码，二维码将在10分钟后过期
     *
     * @param appid 公众号应用id
     * @return 微信公众号官网的登陆二维码
     */
    @Override
    public String syncPre(String appid) {
        WxAccount wxAccount = this.baseMapper.selectById(appid);
        boolean isExpire = WxMpHtmlPageApiUtil.cookieIsExpire(wxAccount);
        synchronized (QrCodeScanState.class) {
            if (isExpire && !QrCodeScanState.qrcodeInfoOf(wxAccount.getAppid()).isPresent()) {
                String qrcodeInfo = WxMpHtmlPageApiUtil.getQRCodeDecodeLinkForce(wxAccount);
                logger.info("{}, {}", wxAccount.getAppid(), qrcodeInfo);
                QrCodeScanState.qrcodeInfo(wxAccount.getAppid(), qrcodeInfo);
                QrCodeScanChangeEvent.publish(wxAccount, QrCodeScanState.WAIT_SCAN);
            }
        }
        return HexUtil.encodeHexStr(QrCodeScanState.qrcodeInfoOf(wxAccount.getAppid()).orElse(""));
    }

    /**
     * 判断当前账号是存在
     *
     * @param appid
     * @return
     */
    private boolean isAccountInRuntime(String appid) {
        try {
            return wxMpService.switchover(appid);
        } catch (NullPointerException e) {// sdk bug，未添加任何账号时configStorageMap为null会出错
            return false;
        }
    }

    /**
     * 添加账号到当前程序，如首次添加需初始化configStorageMap
     *
     * @param entity
     */
    private synchronized void addAccountToRuntime(WxAccount entity) {
        String appid = entity.getAppid();
        WxMpDefaultConfigImpl config = entity.toWxMpConfigStorage();
        try {
            wxMpService.addConfigStorage(appid, config);
        } catch (NullPointerException e) {
            logger.info("需初始化configStorageMap...");
            Map<String, WxMpConfigStorage> configStorages = new HashMap<>(4);
            configStorages.put(appid, config);
            wxMpService.setMultiConfigStorages(configStorages, appid);
        }
    }


}
