package com.github.niefy.modules.wx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.WxMpHtmlPageApiUtil;
import com.github.niefy.modules.wx.entity.WxAccount;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 公众号账号
 *
 * @author niefy
 * @date 2020-06-17 13:56:51
 */
public interface WxAccountService extends IService<WxAccount> {
    /**
     * 分页查询用户数据
     *
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);

    @Override
    boolean save(WxAccount entity);

    /**
     * 全量同步公众号数据
     *
     * @param appid 公众号应用id
     * @return 同步成功与否的标志
     */
    boolean sync(String appid);

    @Override
    boolean removeByIds(Collection<? extends Serializable> idList);

    /**
     * 全量同步公众号数据的前置处理，调用微信公众号官网接口，获取登陆二维码，二维码将在10分钟后过期
     *
     * @param appid 公众号应用id
     * @return 微信公众号官网的登陆二维码
     */
    String syncPre(String appid);

    /**
     * 检查过期时间，如果过期，则更新过期信息到数据库，否则，更新 html token到数据库
     *
     * @param list list of WxAccount
     * @see WxMpHtmlPageApiUtil#cookieIsExpire(com.github.niefy.modules.wx.entity.WxAccount)
     */
    default void checkAndUpdate(List<WxAccount> list) {
        list.forEach(wxAccount -> {
            if (WxMpHtmlPageApiUtil.cookieIsExpire(wxAccount)) {
                wxAccount.setHtmlPageCookiesExpireTime(new Date());
            }
        });
        updateBatchById(list);
    }
}

