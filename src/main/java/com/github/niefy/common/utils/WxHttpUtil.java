package com.github.niefy.common.utils;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.GlobalHeaders;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.github.niefy.common.exception.RRException;
import com.github.niefy.modules.wx.entity.WxAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class WxHttpUtil {

    static {
        GlobalHeaders.INSTANCE.header(Header.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36", true);
    }

    private static final Logger log = LoggerFactory.getLogger(WxHttpUtil.class);

    public static final String MP_WEIXIN_QQ_COM = "https://mp.weixin.qq.com";
    public static final String BASE_URL = MP_WEIXIN_QQ_COM + "/cgi-bin";

    /**
     * 访问指定URL
     *
     * @param wxAccount 当前登陆的账号信息
     * @param url       请求的url
     * @return url的响应结果
     */
    public static String get(WxAccount wxAccount, String url) {
        return request(wxAccount, url, Method.GET);
    }

    /**
     * 以{@link ContentType#JSON}方式提交表单，并返回响应结果
     *
     * @param wxAccount 当前登陆的账号信息
     * @param url       表单提交的url
     * @param body      表单数据
     * @return 提交后的响应结果
     */
    public static String postJSON(WxAccount wxAccount, String url, String body) {
        return post(wxAccount, url, ContentType.JSON, body);
    }

    /**
     * 以{@link ContentType#MULTIPART}方式提交表单，并返回响应结果
     *
     * @param wxAccount 当前登陆的账号信息
     * @param url       表单提交的url
     * @param body      表单数据
     * @return 提交后的响应结果
     */
    public static String postForm(WxAccount wxAccount, String url, Map<String, Object> body) {
        return request(wxAccount, url, Method.POST, req -> {
            req.form(body);
            req.contentType(ContentType.MULTIPART.getValue());
        });
    }

    /**
     * 以{@link ContentType#MULTIPART}方式提交表单，并返回响应结果
     *
     * @param wxAccount 当前登陆的账号信息
     * @param url       表单提交的url
     * @param body      表单数据
     * @return 提交后的响应结果
     */
    public static String postForm(WxAccount wxAccount, String url, Map<String, Object> body, Consumer<HttpRequest> consumer) {
        return request(wxAccount, url, Method.POST, consumer.andThen(req -> {
            req.form(body);
            req.contentType(ContentType.MULTIPART.getValue());
        }));
    }


    /**
     * 以{@link ContentType#FORM_URLENCODED}方式提交表单，并返回响应结果
     *
     * @param wxAccount 当前登陆的账号信息
     * @param url       表单提交的url
     * @param body      表单数据
     * @return 提交后的响应结果
     */
    public static String postFormUrlEncoded(WxAccount wxAccount, String url, Map<String, Object> body) {
        return request(wxAccount, url, Method.POST, req -> {
            req.form(body);
            req.contentType(ContentType.FORM_URLENCODED.getValue());
        });
    }

    /**
     * 以指定的{@link ContentType}方式提交表单，并返回响应结果
     *
     * @param wxAccount   当前登陆的账号信息
     * @param url         表单提交的url
     * @param contentType 用于指定表单的提交方式，支持的类型参见：{@link ContentType}
     * @param body        表单数据
     * @return 提交后的响应结果
     */
    public static String postForm(WxAccount wxAccount, String url, ContentType contentType, String body) {
        return post(wxAccount, url, contentType, body);
    }

    public static String post(WxAccount wxAccount, String url, ContentType contentType, String body) {
        return request(wxAccount, url, Method.POST, req -> req.body(body, contentType.getValue()));
    }


    public static String request(WxAccount wxAccount, String url, Method method) {
        return request(wxAccount, url, method, t -> {
        });
    }


    public static String request(WxAccount wxAccount, String url, Method method, Consumer<HttpRequest> consumer) {
        if (url.contains("${TOKEN}")) {
            url = url.replace("${TOKEN}", wxAccount.getHtmlPageToken());
        }
        if (url.startsWith("http")) {
            String domain = URLUtil.getHost(URLUtil.url(url)).toString();
            if (!MP_WEIXIN_QQ_COM.equals(domain)) {
                log.error("url=[{}] 的域名不是[{}]", url, MP_WEIXIN_QQ_COM);
            }
        } else {
            url = BASE_URL + url;
        }
        String cookies = wxAccount.getHtmlPageCookies();
        HttpRequest req = HttpUtil.createRequest(method, url)
                .setFollowRedirects(true)
                .cookie(cookies);
        consumer.accept(req);
        HttpResponse response = req.execute();
        String body = response.body();
        if (!response.isOk()) {
            log.error("请求的状态不正确，status=[{}], body: {}", response.getStatus(), body);
        }
        Optional.ofNullable(response.header(Header.CONTENT_TYPE))
                .ifPresent(contentType -> {
                    // result is json
                    if (contentType.contains(ContentType.JSON.getValue())) {
                        checkRet(body, req);
                    }
                });
        wxAccount.setHtmlPageCookies(WxMpHtmlPageApiUtil.getCookie(response, cookies));
        wxAccount.setHtmlPageCookiesSyncTime(new Date());
        return body;
    }

    private static void checkRet(String body, HttpRequest req) {
        JSONObject object = JSONObject.parseObject(body);
        Integer ret = null;
        if (object.containsKey("ret")) {
            ret = object.getInteger("ret");
        } else if (object.containsKey("base_resp")) {
            ret = object.getJSONObject("base_resp").getInteger("ret");
        }
        if (ret != null && ret != 0) {
            StringBuilder sb = new StringBuilder();
            Optional.ofNullable(req.form())
                .ifPresent(form->form.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n")));
	    log.error("request:{},\n form:\n{}\n result err info: {}", req, sb, body);
            throw new RRException(WxHtmlArticleRetErrCode.msg(ret), ret);
        }
    }
}
