package com.github.niefy.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * IP地址
 * @author Mark sunlightcs@gmail.com
 */
public class IPUtils {
    private static Logger logger = LoggerFactory.getLogger(IPUtils.class);
    private static final String UNKNOWN = "unknown";

    /**
     * 获取IP地址
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (!StringUtils.hasText(ip) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            logger.error("IPUtils ERROR ", e);
        }

        return ip;
    }

    /**
     * 判断是否是本地请求
     * @return 是否为本地请求
     */
    public static boolean isLocalRequest(){
        String ipAddr = IPUtils.getIpAddr(HttpContextUtils.getHttpServletRequest());
        return Arrays.asList("127.0.0.1","::1","00:00:00:00:00:01").contains(ipAddr);
    }
    /**
     * 判断是否不是本地请求
     * @return 是否不为本地请求
     */
    public static boolean isNotLocalRequest(){
        return !isLocalRequest();
    }

}
