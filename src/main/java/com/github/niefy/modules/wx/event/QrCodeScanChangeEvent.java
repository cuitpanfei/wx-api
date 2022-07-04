package com.github.niefy.modules.wx.event;

import cn.hutool.core.thread.ThreadUtil;
import com.github.niefy.common.utils.SpringContextUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.util.Assert;

public class QrCodeScanChangeEvent<S extends QrCodeScanState> extends PayloadApplicationEvent<S> {
    /**
     * Create a new PayloadApplicationEvent.
     *
     * @param source  the object on which the event initially occurred (never {@code null})
     * @param payload the payload object (never {@code null})
     */
    public QrCodeScanChangeEvent(Object source, S payload) {
        super(source, payload);
    }


    public static <S extends QrCodeScanState> void publish(Object source, S state) {
        ApplicationContext context = SpringContextUtils.applicationContext;
        publish(context, source, state);
    }

    public static <S extends QrCodeScanState> void publish(ApplicationContext context, Object source, S state) {
        Assert.notNull(context, "Context must not be null");
        publish((ApplicationEventPublisher) context, source, state);
    }

    public static <S extends QrCodeScanState> void publish(ApplicationEventPublisher publisher, Object source,
                                                           S state) {
        Assert.notNull(publisher, "Publisher must not be null");
        ThreadUtil.execute(() -> publisher.publishEvent(new QrCodeScanChangeEvent<>(source, state)));
    }
}
