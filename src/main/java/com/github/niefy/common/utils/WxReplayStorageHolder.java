package com.github.niefy.common.utils;

import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WxReplayStorageHolder {
    private static final ThreadLocal<Map<String, WxMpXmlOutMessage>> THREAD_LOCAL = ThreadLocal.withInitial(() -> new ConcurrentHashMap<>(16));

    public static WxMpXmlOutMessage get() {
        return THREAD_LOCAL.get().get(WxMpConfigStorageHolder.get());
    }
    public static WxMpXmlOutMessage pop() {
        return THREAD_LOCAL.get().remove(WxMpConfigStorageHolder.get());
    }

    public static void set(WxMpXmlOutMessage msg) {
        THREAD_LOCAL.get().put(WxMpConfigStorageHolder.get(), msg);
    }

    /**
     * 此方法需要用户根据自己程序代码，在适当位置手动触发调用，本SDK里无法判断调用时机
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }

    /**
     * 此方法需要用户根据自己程序代码，在适当位置手动触发调用，本SDK里无法判断调用时机
     */
    public static void clear() {
        THREAD_LOCAL.get().clear();
    }

    protected WxReplayStorageHolder() {
    }

}
