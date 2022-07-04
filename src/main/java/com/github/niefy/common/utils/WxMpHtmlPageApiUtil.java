package com.github.niefy.common.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.niefy.modules.wx.entity.Article;
import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.event.QrCodeScanState;
import com.github.niefy.modules.wx.form.draft.AppmsgAlbumInfo;
import com.github.niefy.modules.wx.form.draft.DraftInfos;
import com.github.niefy.modules.wx.form.draft.MultiItem;
import com.github.niefy.modules.wx.form.draft.NewsItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static org.apache.http.cookie.SM.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public class WxMpHtmlPageApiUtil {

    private static final GsonBuilder INSTANCE = new GsonBuilder();
    public static final Gson GSON_INSTANCE = INSTANCE.create();

    private static final Logger log = LoggerFactory.getLogger(WxMpHtmlPageApiUtil.class);

    public static final String MP_WEIXIN_QQ_COM = WxHttpUtil.MP_WEIXIN_QQ_COM;
    public static final String BIZLOGIN_URL = WxHttpUtil.BASE_URL + "/bizlogin";
    public static final String WRITER_MGR_URL = WxHttpUtil.MP_WEIXIN_QQ_COM + "/acct/writermgr?action=search&token=${TOKEN}&lang=zh_CN&f=json&ajax=1&random=${RANDOM}&author=${AUTHOR}&writerids=";
    public static final String CROP_MULTI = WxHttpUtil.BASE_URL + "/cropimage?action=crop_multi";
    public static final String NEW_DRAFT_PAGE_URL = WxHttpUtil.BASE_URL + "/appmsg?t=media/appmsg_edit_v2&action=edit&isNew=1&type=77&token=${TOKEN}&lang=zh_CN";
    public static final String NEW_DRAFT_SUBMIT_URL = WxHttpUtil.BASE_URL + "/operate_appmsg?t=ajax-response&sub=${SUB_TYPE}&type=77&token=${TOKEN}&lang=zh_CN";
    public static final String DRAFT_INFO_URL = WxHttpUtil.BASE_URL + "/appmsg?t=media/appmsg_edit&action=edit&type=77&appmsgid=${APPMSGID}&token=${TOKEN}&lang=zh_CN";
    public static final String SEARCH_BIZ = WxHttpUtil.BASE_URL + "/searchbiz?action=search_biz&scene=1&begin=0&count=10&query=${QUERY}&token=${TOKEN}&lang=zh_CN&f=json&ajax=1";
    public static final String PHOTO_GALLERY = WxHttpUtil.BASE_URL + "/photogallery?action=search&query=${QUERY}&type=1&limit=16&last_seq=0&token=${TOKEN}&lang=zh_CN&f=json&ajax=1";
    public static final String UPLOAD_IMG_TO_CDN = WxHttpUtil.BASE_URL + "/uploadimg2cdn?lang=zh_CN&token=${TOKEN}&t=";

    private static final String MP_PROFILE_CODE_TEMPLATE = "<section class=\"mp_profile_iframe_wrp\"><mpprofile " +
            "class=\"js_uneditable custom_select_card mp_profile_iframe\" data-pluginname=\"mpprofile\" " +
            "data-id=\"${ID}\" data-headimg=\"${HEAD_IMG}\" data-nickname=\"${NICK_NAME}\" data-alias=\"${ALIAS}\" " +
            "data-signature=\"${SIGNATURE}\" data-from=\"0\" contenteditable=\"false\"></mpprofile></section>";

    private static String mpProfileCode(WxAccount wxAccount) {
        JSONObject mpProfile = JSONObject.parseObject(mpProfile(wxAccount));
        JSONObject profileInfo = (JSONObject) mpProfile.getJSONArray("list").get(0);
        return MP_PROFILE_CODE_TEMPLATE.replace("${ID}", profileInfo.getString("fakeid"))
                .replace("${HEAD_IMG}", profileInfo.getString("round_head_img"))
                .replace("${NICK_NAME}", profileInfo.getString("nickname"))
                .replace("${ALIAS}", profileInfo.getString("alias"))
                .replace("${SIGNATURE}", profileInfo.getString("signature"));
    }

    /**
     * <p>响应结果：</p>
     * <pre>
     *     <code>
     *
     * {
     * 	"base_resp": {
     * 		"err_msg": "ok",
     * 		"ret": 0
     *        },
     * 	"list": [
     *        {
     *          alias: ""
     *          fakeid: ""
     *          nickname: "飞羽英华"
     *          round_head_img: ""
     *          service_type: 1
     *          signature: "个人分享"
     *        }
     * 	],
     * 	"total": 1
     * }
     *     </code>
     * </pre>
     *
     * @param wxAccount
     * @return
     */
    private static String mpProfile(WxAccount wxAccount) {
        return WxHttpUtil.get(wxAccount, SEARCH_BIZ.replace("${QUERY}", "%E9%A3%9E%E7%BE%BD%E8%8B%B1%E5%8D%8E"));
    }

    public static String getCookie(HttpResponse response, String oldCookies) {
        Set<String> cookieSet = Arrays.stream(oldCookies.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        Optional.ofNullable(response.headerList(SET_COOKIE))
                .orElse(Collections.emptyList())
                .stream()
                .filter(s -> !s.contains("EXPIRED"))
                .map(s -> s.contains(";") ? s.split(";")[0].trim() : s.trim())
                .forEach(cookieSet::add);
        return join("; ", cookieSet);
    }

    public static String prelogin(String cookies) {
        HttpResponse response = HttpUtil.createPost(BIZLOGIN_URL)
                .cookie(cookies)
                .header(Header.REFERER, MP_WEIXIN_QQ_COM)
                .body("action=prelogin&token=&lang=zh_CN&f=json&ajax=1", APPLICATION_FORM_URLENCODED_VALUE)
                .execute();
        log.info("prelogin, result body:[{}]", response.body());
        return getCookie(response, cookies);
    }

    public static String startlogin(String cookies) {
        HttpResponse response = HttpUtil.createPost("https://mp.weixin.qq.com/cgi-bin/bizlogin?action=startlogin")
                .cookie(cookies)
                .header(Header.REFERER, MP_WEIXIN_QQ_COM)
                .body("userlang=zh_CN&redirect_url=&login_type=3&sessionid=" + System.currentTimeMillis()
                        + "0&token=&lang=zh_CN&f=json&ajax=1", "application/x-www-form-urlencoded; charset=UTF-8")
                .execute();
        log.info("startlogin, result body:[{}]", response.body());
        return getCookie(response, cookies);
    }

    /**
     * 获取登陆后的详细数据，如：
     * <pre>
     *     <code>
     *         wx.cgiData = {
     *                         timesend_msg : "{\"sent_list\":[]}",
     *                         nickname_invade : "2" * 1, // 是否名称侵权
     *                         apply_old_advert : "",
     *                         apply_new_advert : "",
     *                         wxverify_annual_review : "" * 1 || 0,
     *                         wxverify_expired_time : "" * 1,
     *                         total_count : "229",
     *                         count : "1",
     *                         begin : "1",
     *                         bank_deadline : "0" * 1,
     *                         force_remit_verify : "0" * 1, // 强制显示小额打款
     *                         bank_verify_status : "0" * 1,
     *                         register_type : "0" * 1, // 0=小额打款，1=注册后认证
     *                         show_verify_warning : '0' * 1,
     *                         can_submit_wxverify_remit_info : '0' * 1,
     *                         verify_deadline : '0' * 1, // 注册后认证的最后期限
     *                         remit_code_prefix : '', // 小额打款验证码前缀
     *                         exist_appsecret_danger : '0' * 1,  // 用户是否有AppSecret泄露的危险，1为有，0为无
     *
     *                         realname_type: '0' * 1, // 0 个人类型，1 非个人类型，2 媒体帐号
     *                         // is_writeoff_pay: '' * 1 || 0, // 组织类的注销是否已支付
     *                         is_writeoff_timeout: '1', // 组织类的注销是否支付超时
     *                         remit_info: {
     *                             deadline: '',
     *                             money: '' / 100,
     *                             remit_bank_no: '',
     *                             remit_code: '',
     *                         },
     *                         principal_name: '',
     *                         new_recent_edit:,
     *                         can_use_public_tag: '1' * 1,
     *                         can_use_album_send: '1' * 1,
     *                         service_type: '1' * 1,
     *                         can_use_release_video: '0' * 1,
     *                         customer_type: '0' * 1, // 11: 认证事业机构账号
     *                         is_mp_finder_gray_user: "0" * 1,  // 一期视频号灰度用户，已废弃
     *                         show_finder_windows: '1' * 1,   // 是否显示视频号消息提示卡片
     *                         show_finder_red_dot: '1' * 1,   // 是否显示视频号消息红点
     *                         show_finder_live_reddot: '1' * 1
     *                     };
     *
     *                     wx.cgiData.notice = {
     *                         userRole:'25',
     *                         notifyMsg:''
     *                     };
     *
     *                     // 强制校验身份
     *                     wx.cgiData.force_check_info = {
     *                                 is_force_check: '0' * 1,
     *                         expire_time: '' * 1,
     *                         id_name: '',
     *                         id_card: '',
     *                         check_status: ''
     *                             };
     *
     *                     // 强制提交材料
     *                     wx.cgiData.force_update_material = {
     *                         need_update: ""==1||""==3||""==2,
     *                         status: ""*1,
     *                         expire_time: ""
     *                     };
     *
     *                     wx.cgiData.nick_name = "飞羽英华";
     *
     *                     wx.cgiData.force_do_qq_upgrade = "";
     *
     *                     // 之所以这样写是因为点击视频发布跳转到首页之后 masssend_page 不存在，用 release_page 替代
     *                     wx.cgiData.mass_data = {"sent_list":[],"total_count":4};
     *                     wx.cgiData.service_type = 1;
     *                     // wx.cgiData.mass_send_left = 1;
     *                     // wx.cgiData.client_time_diff = "1646114008" * 1 - Math.floor(new Date().getTime()/1000);
     *
     *                     try{
     *                         wx.cgiData.timesend_msg = JSON.parse(wx.cgiData.timesend_msg);
     *                         wx.cgiData.timesend_msg = wx.cgiData.timesend_msg.sent_list||[];
     *                     }catch(e){
     *                         wx.cgiData.timesend_msg = [];
     *                     }
     *                     wx.data = wx.commonData.data;
     *                     /*wx.cgiData.mass_data.sent_list[0].sent_result.msg_status=6;
     *                     wx.cgiData.mass_data.sent_list[0].sent_result.reject_index_list=[0];
     *     </code>
     * </pre>
     *
     * @param cookies
     * @return
     */
    public static JSONObject login(String cookies) {
        HttpResponse response = HttpUtil.createPost("https://mp.weixin.qq.com/cgi-bin/bizlogin?action=login")
                .cookie(cookies)
                .header(Header.REFERER, MP_WEIXIN_QQ_COM)
                .body("userlang=zh_CN&redirect_url=&cookie_forbidden=0&cookie_cleaned=1&plugin_used=0&login_type=3&token=&lang=zh_CN&f=json&ajax=1", "application/x-www-form-urlencoded; charset=UTF-8")
                .execute();
        String jsonResult = response.body();
        JSONObject jsonObject = JSONObject.parseObject(jsonResult);
        String loginCookies = getCookie(response, cookies);
        JSONObject result = new JSONObject().fluentPut("cookies", loginCookies);
        if (response.isOk()) {
            String redirectUrl = jsonObject.getString("redirect_url");
            if (redirectUrl != null) {
                result.put("token", redirectUrl.replaceAll("\\D+(\\d+)\\D*", "$1"));
            }
            HttpResponse httpResponse = HttpUtil.createGet(MP_WEIXIN_QQ_COM + redirectUrl)
                    .cookie(cookies)
                    .header(Header.REFERER, redirectUrl)
                    .execute();
            if (!httpResponse.isOk()) {
                result.put("errorMsg", httpResponse.body());
            }
        }
        return result;
    }

    /**
     * 检查wxAccount的cookie是否过期
     *
     * @param wxAccount wxAccount
     * @return true on not expire, otherwise, false.
     */
    public static boolean cookieIsExpire(WxAccount wxAccount) {
        return !cookieIsNotExpire(wxAccount);
    }

    public static boolean cookieIsNotExpire(WxAccount wxAccount) {
        String cookies = wxAccount.getHtmlPageCookies();
        Optional<String> token = cookieIsNotExpire(cookies);
        token.ifPresent(wxAccount::setHtmlPageToken);
        return token.isPresent();
    }

    /**
     * 公众平台官网 - 网页版的cookie是否过期
     *
     * @param cookies cookies
     * @return true on not expire, otherwise, false.
     */
    public static Optional<String> cookieIsNotExpire(String cookies) {
        HttpResponse response = HttpUtil.createGet(MP_WEIXIN_QQ_COM)
                .setFollowRedirects(true)
                .cookie(cookies)
                .execute();
        String body = response.body();
        return Arrays.stream(body.split("[\r]?\n"))
                .filter(s -> s.matches("[ ]+token: '(\\d+)',$"))
                .findFirst()
                .map(s -> s.replaceAll("[ ]+token: '(\\d+)',$", "$1"));
    }

    /**
     * 强制获取登陆二维码解析的链接，将在10分钟后过期
     *
     * @return 二维码解析的链接
     */
    public static String getQRCodeDecodeLinkForce(WxAccount wxAccount) {
        HttpResponse response = HttpUtil.createGet(MP_WEIXIN_QQ_COM)
                .setFollowRedirects(true)
                .execute();
        String cookie = getCookie(response, "");
        cookie = prelogin(cookie);
        cookie = startlogin(cookie);
        wxAccount.setHtmlPageCookies(cookie);
        return getQRCodeDecodeLink(wxAccount);
    }

    /**
     * 获取登陆二维码解析的链接，将在10分钟后过期
     *
     * @param wxAccount
     * @return 二维码解析的链接
     */
    public static String getQRCodeDecodeLink(WxAccount wxAccount) {
        String cookies = Optional.ofNullable(wxAccount.getHtmlPageCookies()).orElse("");
        HttpRequest request = HttpUtil.createGet("https://mp.weixin.qq.com/cgi-bin/scanloginqrcode?action=getqrcode&random=" + System.currentTimeMillis())
                .header(Header.REFERER, MP_WEIXIN_QQ_COM);
        if (!cookies.contains("wxuin")) {
            cookies = cookies + "; wxuin=" + getNewWxUin();
        }
        HttpResponse response = request.cookie(cookies).execute();
        String qrcodeInfo;
        if (response.isOk()) {
            File file = FileUtil.writeFromStream(response.bodyStream(), "qrcodeInfo.png");
            qrcodeInfo = QrCodeUtil.decode(FileUtil.getInputStream(file));
        } else {
            qrcodeInfo = null;
        }
        cookies = getCookie(response, cookies);
        wxAccount.setHtmlPageCookies(cookies);
        return qrcodeInfo;
    }

    public static QrCodeScanState scanloginqrcodeAck(String cookies) {
        HttpResponse response = HttpUtil.createGet("https://mp.weixin.qq.com/cgi-bin/scanloginqrcode?action=ask&token=&lang=zh_CN&f=json&ajax=1")
                .cookie(cookies)
                .header(Header.REFERER, MP_WEIXIN_QQ_COM)
                .execute();
        String jsonResult = response.body();
        log.info("scanloginqrcode ack, result body:[{}]", jsonResult);
        JSONObject jsonObject = JSONObject.parseObject(jsonResult);
        Integer retCode = jsonObject.getJSONObject("base_resp").getInteger("ret");
        if (retCode != null && retCode == 0) {
            int status = jsonObject.getIntValue("status");
            return QrCodeScanState.statusOf(status);
        } else {
            log.info("ack 发现扫码结果失败, result=[{}]", jsonResult);
        }
        return QrCodeScanState.UN_KNOW;
    }

    public static String getNewWxUin() {
        return String.valueOf(System.nanoTime()).substring(2);
    }

    public static void main(String[] args) {
        String oldCookie = "appmsglist_action_3895151075=card; wxuin=46385954066622; ua_id=hmpfVU5Kcs7mLqIRAAAAAE3kpWcPckX_5rnIT6M995k=; pgv_pvid=7231114880; pac_uid=0_9a327fe673802; ts_uid=9968689696; mm_lang=zh_CN; RK=Q5kdeGe0e3; ptcz=849f89b27f4038da3c8c07aaf4c3d6ebf21bec5a4c1a3927aec02531f4ab7f31; rewardsn=; wxtokenkey=777; pgv_info=ssid=s425635856; _qpsvr_localtk=0.0986151363664316; uin=o1730789103; skey=@UnqKnlEhN; uuid=6c098b729576d80cae1ce05278eb666e; rand_info=CAESIJvgOXEbDqqp++IFwSaNmxXkWMWOfYhqzibdG3oDVmE7; slave_bizuin=3895151075; data_bizuin=3895151075; bizuin=3895151075; data_ticket=FRIgYs32E73dJjCWKMKzN1Q+6o8xRnZ8TuKuAhS/lethUzpTJc6gw5TZ5bSsztKj; slave_sid=RklKbWZ6dkh5b1pLekIzNzgxUWxMUXVPOHF6VmZhSHRobG9qcE5sMHpPUDNTVXc0OTdiSWJvamlKbko5OTloSEdxZVVNRUpjRTdYTVdURHhQSV9icVV5SnRobzNBZUxKM0JERzRTS0NJQ242T1pxZk16d2d1eXpQcGdVQXlNWjZ5R2drN2lkZE13RTJHSGNU; slave_user=gh_2b50a79cdb27; xid=920a7ddce25a5d1a079b551e0be7c3e5";
        HttpResponse response = HttpUtil.createGet(MP_WEIXIN_QQ_COM)
                .setFollowRedirects(true)
                .cookie(oldCookie)
                .execute();

        String cookie = getCookie(response, oldCookie);
        String body = response.body();
        WxAccount wxAccount = JSON.parseObject("{\"appid\":\"wx048597a70e4a6035\",\"name\":\"飞羽英华\",\"type\":1," +
                "\"verified\":false,\"secret\":\"027be75ed612cdffb6912a8c9377e6f9\",\"token\":\"cuitpanfeidtoken\"," +
                "\"aesKey\":\"GsnTe1ynnxYqit1fO0ZRYphbjL9W2s4LtdZoUrxTxfX\"," +
                "\"htmlPageCookiesSyncTime\":\"2022-06-23 11:17\",\"htmlPageCookiesExpireTime\":\"2022-06-28 15:01\"," +
                "}", WxAccount.class);
        JSONObject result = new JSONObject();
        if (body.contains("近期编辑")) {
            String substring = body.substring(body.indexOf("var baseQuery = {"));
            substring = substring.substring(substring.indexOf("token"), substring.indexOf(","));
            String token = substring.substring(substring.indexOf("'") + 1, substring.lastIndexOf("'"));
            result.fluentPut("cookies", cookie)
                    .fluentPut("token", token);
            wxAccount.setHtmlPageCookies(cookie);
            wxAccount.setHtmlPageToken(token);
            System.out.println(result);
        } else {
            cookie = prelogin(cookie);
            cookie = startlogin(cookie);

            wxAccount.setHtmlPageCookies(cookie);
            getQRCodeDecodeLink(wxAccount);
            cookie = wxAccount.getHtmlPageCookies();
            QrCodeScanState qrCodeScanState;
            do {
                qrCodeScanState = scanloginqrcodeAck(cookie);
                log.info("当前状态：{}", qrCodeScanState);
                ThreadUtil.sleep(1000);
            } while (qrCodeScanState != QrCodeScanState.DONE && qrCodeScanState != QrCodeScanState.UN_KNOW);

            result = login(cookie);
        }
        wxAccount.setHtmlPageCookies(result.getString("cookies"));
        wxAccount.setHtmlPageToken(result.getString("token"));
        Article article = JSON.toJavaObject(JSON.parseObject("{\"id\":4,\"type\":1,\"title\":\"公众号文章分类2\"," +
                "\"tags\":\"\",\"summary\":\"\",\"content\":\"```json\\n[{\\n        \\\"title\\\": \\\"时事\\\",\\n        \\\"sublist\\\": [\\\"国家领导人\\\", \\\"反腐新闻\\\", \\\"官员任免\\\", \\\"台湾\\\", \\\"港澳\\\", \\\"新时代\\\", \\\"中国外交\\\", \\\"美国\\\", \\\"俄罗斯\\\", \\\"日本\\\", \\\"欧洲/欧盟\\\", \\\"东南亚\\\", \\\"中东\\\", \\\"朝韩\\\", \\\"美洲\\\", \\\"非洲\\\", \\\"亚洲\\\", \\\"国际组织\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"社会\\\",\\n        \\\"sublist\\\": [\\\"民生\\\", \\\"民生政策\\\", \\\"法律法规\\\", \\\"法制纠纷\\\", \\\"事故灾害\\\", \\\"公益\\\", \\\"正能量\\\", \\\"校园新闻\\\", \\\"社区新闻\\\", \\\"奇闻趣事\\\", \\\"组织内部管理\\\", \\\"海外民生\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"财经\\\",\\n        \\\"sublist\\\": [\\\"宏观经济\\\", \\\"产业经济\\\", \\\"投资/创业\\\", \\\"公司新闻\\\", \\\"财经人物\\\", \\\"理财投资技巧\\\", \\\"股票\\\", \\\"期货\\\", \\\"银行\\\", \\\"基金\\\", \\\"保险\\\", \\\"外汇\\\", \\\"债券\\\", \\\"信托\\\", \\\"贵金属\\\", \\\"能源\\\", \\\"数字货币\\\", \\\"收藏\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"军事\\\",\\n        \\\"sublist\\\": [\\\"武器装备\\\", \\\"军用飞行器\\\", \\\"军事历史\\\", \\\"军情/军事新闻\\\", \\\"军事知识\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"教育\\\",\\n        \\\"sublist\\\": [\\\"学前教育\\\", \\\"小学教育\\\", \\\"初中教育\\\", \\\"高中教育\\\", \\\"高等教育\\\", \\\"语言学习\\\", \\\"留学相关\\\", \\\"职业考试\\\", \\\"技能教育\\\", \\\"演讲\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"体育\\\",\\n        \\\"sublist\\\": [\\\"NBA\\\", \\\"CBA\\\", \\\"篮球其他\\\", \\\"国际足球\\\", \\\"国内足球\\\", \\\"足球其他\\\", \\\"综合体育\\\", \\\"冬季项目\\\", \\\"大众体育\\\", \\\"户外运动\\\", \\\"极限运动\\\", \\\"大型赛事\\\", \\\"搏击\\\", \\\"精英体育\\\", \\\"彩票\\\", \\\"棋牌\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"科技\\\",\\n        \\\"sublist\\\": [\\\"科技股公司\\\", \\\"科技创投\\\", \\\"科技大佬\\\", \\\"互联网坊间八卦\\\", \\\"内容产业\\\", \\\"互联网金融\\\", \\\"互联网+\\\", \\\"通信\\\", \\\"区块链\\\", \\\"前沿产业(云/AR/VR等)\\\", \\\"信息技术\\\", \\\"电商\\\", \\\"软件工具\\\", \\\"数码\\\", \\\"汽车科技\\\", \\\"科技奇趣\\\", \\\"航空航天/机械\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"汽车\\\",\\n        \\\"sublist\\\": [\\\"汽车产业\\\", \\\"新车资讯\\\", \\\"汽车评测\\\", \\\"汽车导购\\\", \\\"车展资讯\\\", \\\"二手车\\\", \\\"保养/开车技巧\\\", \\\"汽车改装\\\", \\\"汽车模型\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"科学\\\",\\n        \\\"sublist\\\": [\\\"天文宇宙\\\", \\\"地理\\\", \\\"动植物世界\\\", \\\"医学\\\", \\\"生物考古\\\", \\\"科普\\\", \\\"科幻\\\", \\\"科学实验\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"房产\\\",\\n        \\\"sublist\\\": [\\\"房产政策\\\", \\\"房价走势\\\", \\\"买房相关\\\", \\\"卖房相关\\\", \\\"租房相关\\\", \\\"海外房产\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"电影\\\",\\n        \\\"sublist\\\": [\\\"电影资讯\\\", \\\"电影解说/片段\\\", \\\"电影混剪\\\", \\\"预告花絮\\\", \\\"微电影\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"电视剧\\\",\\n        \\\"sublist\\\": [\\\"预告资讯\\\", \\\"精彩片段\\\", \\\"电视剧解说\\\", \\\"自制混剪\\\", \\\"恶搞配音\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"综艺\\\",\\n        \\\"sublist\\\": [\\\"内地综艺\\\", \\\"港台综艺\\\", \\\"韩国综艺\\\", \\\"日本综艺\\\", \\\"欧美综艺\\\", \\\"晚会盛典\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"明星\\\",\\n        \\\"sublist\\\": [\\\"八卦\\\", \\\"内地明星\\\", \\\"港台明星\\\", \\\"日本明星\\\", \\\"韩国明星\\\", \\\"欧美明星\\\", \\\"其他地区明星\\\", \\\"明星直发\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"音乐\\\",\\n        \\\"sublist\\\": [\\\"流行\\\", \\\"电子\\\", \\\"轻音乐\\\", \\\"民谣\\\", \\\"说唱\\\", \\\"摇滚\\\", \\\"爵士\\\", \\\"R&B\\\", \\\"布鲁斯\\\", \\\"古典\\\", \\\"网络歌曲\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"动漫\\\",\\n        \\\"sublist\\\": [\\\"恋爱\\\", \\\"剧情\\\", \\\"日常\\\", \\\"恐怖\\\", \\\"犯罪\\\", \\\"悬疑\\\", \\\"武侠\\\", \\\"历史\\\", \\\"冒险\\\", \\\"动作\\\", \\\"竞技\\\", \\\"玄幻\\\", \\\"奇幻\\\", \\\"异能\\\", \\\"科幻\\\", \\\"原创短片\\\", \\\"盘点解读\\\", \\\"虚拟偶像\\\", \\\"声优资讯\\\", \\\"漫展资讯\\\", \\\"动漫壁纸\\\", \\\"cosplay\\\", \\\"鬼畜\\\", \\\"宅舞\\\", \\\"手绘\\\", \\\"MMD\\\", \\\"AMV/MAD/手书\\\", \\\"动漫音乐\\\", \\\"动漫相关资讯\\\", \\\"手办\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"游戏\\\",\\n        \\\"sublist\\\": [\\\"手游\\\", \\\"小游戏\\\", \\\"主机游戏\\\", \\\"端游\\\", \\\"页游\\\", \\\"街机游戏\\\", \\\"便携游戏\\\", \\\"电竞赛事\\\", \\\"游戏展会\\\", \\\"游戏泛资讯\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"时尚\\\",\\n        \\\"sublist\\\": [\\\"穿搭\\\", \\\"街拍\\\", \\\"美妆\\\", \\\"美体整形/医美\\\", \\\"时尚好物\\\", \\\"男士时尚\\\", \\\"时尚资讯/活动\\\", \\\"时尚大片\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"健康\\\",\\n        \\\"sublist\\\": [\\\"养生保健\\\", \\\"健康科普知识\\\", \\\"心理健康\\\", \\\"饮食健康\\\", \\\"医疗\\\", \\\"药品\\\", \\\"疾病防治\\\", \\\"癌症\\\", \\\"口腔健康\\\", \\\"两性健康\\\", \\\"男性健康\\\", \\\"女性健康\\\", \\\"老人健康\\\", \\\"中医\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"旅游\\\",\\n        \\\"sublist\\\": [\\\"自由行\\\", \\\"跟团游\\\", \\\"一日游/周末游\\\", \\\"亲子游\\\", \\\"定制游\\\", \\\"邮轮游\\\", \\\"穷游\\\", \\\"海岛\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"美食\\\",\\n        \\\"sublist\\\": [\\\"探店攻略\\\", \\\"菜谱\\\", \\\"美食文化\\\", \\\"美食猎奇\\\", \\\"吃播\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"生活\\\",\\n        \\\"sublist\\\": [\\\"生活百科\\\", \\\"生活方式\\\", \\\"日用家居\\\", \\\"购物信息\\\", \\\"生活记录\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"摄影\\\",\\n        \\\"sublist\\\": [\\\"摄影作品\\\", \\\"摄影技术\\\", \\\"摄影器材\\\", \\\"后期处理\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"宠物\\\",\\n        \\\"sublist\\\": [\\\"狗\\\", \\\"猫\\\", \\\"仓鼠\\\", \\\"兔子\\\", \\\"猪\\\", \\\"鱼\\\", \\\"鸟\\\", \\\"乌龟\\\", \\\"龙猫\\\", \\\"蛇\\\", \\\"蜥蜴\\\", \\\"蜘蛛\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"职场\\\",\\n        \\\"sublist\\\": [\\\"职场经验\\\", \\\"求职面试\\\", \\\"商业管理\\\", \\\"创业指导\\\", \\\"网店经营\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"育儿\\\",\\n        \\\"sublist\\\": [\\\"萌宝晒娃\\\", \\\"亲子成长\\\", \\\"备孕\\\", \\\"孕产\\\", \\\"产后\\\", \\\"饮食\\\", \\\"疾病防治\\\", \\\"护理发育\\\", \\\"母婴用品\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"情感\\\",\\n        \\\"sublist\\\": [\\\"两性关系\\\", \\\"家庭关系\\\", \\\"单身\\\", \\\"人际关系\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"心灵鸡汤\\\",\\n        \\\"sublist\\\": [\\\"人生感悟\\\", \\\"名人名言\\\", \\\"美文\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"星座命理\\\",\\n        \\\"sublist\\\": [\\\"星座\\\", \\\"生肖\\\", \\\"命理\\\", \\\"风水\\\", \\\"相术\\\", \\\"血型\\\", \\\"塔罗牌\\\", \\\"测试\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"小说\\\",\\n        \\\"sublist\\\": [\\\"东方玄幻\\\", \\\"都市小说\\\", \\\"仙侠武侠\\\", \\\"异界大陆\\\", \\\"异术超能\\\", \\\"西方奇幻\\\", \\\"同人文\\\", \\\"历史/历史架空\\\", \\\"游戏竞技\\\", \\\"科幻未来\\\", \\\"时空穿梭\\\", \\\"悬疑恐怖\\\", \\\"军事小说\\\", \\\"穿越架空\\\", \\\"豪门世家\\\", \\\"现代言情\\\", \\\"玄幻言情\\\", \\\"古代言情\\\", \\\"快穿小说\\\", \\\"宫闱宅斗\\\", \\\"仙侠奇缘\\\", \\\"异术超能\\\", \\\"种田文\\\", \\\"校园言情\\\", \\\"科幻神秘\\\", \\\"同人文\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"曲艺\\\",\\n        \\\"sublist\\\": [\\\"相声\\\", \\\"小品\\\", \\\"评书\\\", \\\"京剧\\\", \\\"越剧\\\", \\\"秦腔\\\", \\\"昆曲\\\", \\\"豫剧\\\", \\\"粤剧\\\", \\\"潮剧\\\", \\\"黄梅戏\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"文化\\\",\\n        \\\"sublist\\\": [\\\"文学\\\", \\\"绘画\\\", \\\"舞蹈\\\", \\\"音乐\\\", \\\"雕塑\\\", \\\"建筑\\\", \\\"戏剧\\\", \\\"工艺\\\", \\\"书法\\\", \\\"民俗\\\", \\\"哲学\\\", \\\"国学\\\", \\\"宗教\\\", \\\"收藏\\\", \\\"文物\\\", \\\"读书\\\", \\\"综合文化\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"历史\\\",\\n        \\\"sublist\\\": [\\\"中国古代史\\\", \\\"中国近代史\\\", \\\"中国当代史\\\", \\\"党史\\\", \\\"世界史\\\", \\\"野史八卦\\\", \\\"历史考古\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"三农\\\",\\n        \\\"sublist\\\": [\\\"农村\\\", \\\"农业\\\", \\\"农民\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"广告创意\\\",\\n        \\\"sublist\\\": [\\\"创意广告\\\", \\\"公益广告\\\", \\\"品牌广告\\\", \\\"宣传短片\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"纪录片\\\",\\n        \\\"sublist\\\": [\\\"美食纪录片\\\", \\\"自然纪录片\\\", \\\"科学纪录片\\\", \\\"历史纪录片\\\", \\\"旅游纪录片\\\", \\\"文化纪录片\\\", \\\"人物纪录片\\\", \\\"军事纪录片\\\", \\\"工业纪录片\\\", \\\"主旋律纪录片\\\", \\\"社会现实题材纪录片\\\"]\\n    }, {\\n        \\\"title\\\": \\\"搞笑\\\",\\n        \\\"sublist\\\": [\\\"段子\\\", \\\"神回复/神操作\\\", \\\"糗事\\\", \\\"内涵图\\\", \\\"神剪辑\\\", \\\"整蛊/搞怪\\\", \\\"表演秀\\\", \\\"搞笑影视\\\", \\\"熊孩子\\\", \\\"爆笑动物\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"壁纸头像\\\",\\n        \\\"sublist\\\": [\\\"表情包\\\", \\\"套图\\\", \\\"壁纸\\\", \\\"头像\\\", \\\"文字图\\\", \\\"其他\\\"]\\n    }, {\\n        \\\"title\\\": \\\"其他\\\",\\n        \\\"sublist\\\": []\\n    }\\n]\\n\\n```\",\"contentHtml\":null,\"mediaId\":null,\"appMsgId\":null,\"category\":\"公众号\",\"subCategory\":\"文章\",\"createTime\":\"2022-06-24 20:31\",\"updateTime\":\"2022-06-24 20:36\",\"openCount\":21,\"targetLink\":\"https://wx.pfinfo.com.cnwx/article/detail?articleId=4\",\"image\":\"\"}"), Article.class);
        article.setCategory("其他");
        String page = postNewDraftPage(wxAccount, 100000141L, Arrays.asList("<div><section class=\"code-snippet__fix " +
                        "code-snippet__js\"><ul class=\"code-snippet__line-index code-snippet__js\"><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li></ul><pre class=\"code-snippet__js\" data-lang=\"json\"><code><span class=\"code-snippet_outer\">[{</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"时事\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"国家领导人\", \"反腐新闻\", \"官员任免\", \"台湾\", \"港澳\", \"新时代\", \"中国外交\", \"美国\", \"俄罗斯\", \"日本\", \"欧洲/欧盟\", \"东南亚\", \"中东\", \"朝韩\", \"美洲\", \"非洲\", \"亚洲\", \"国际组织\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"社会\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"民生\", \"民生政策\", \"法律法规\", \"法制纠纷\", \"事故灾害\", \"公益\", \"正能量\", \"校园新闻\", \"社区新闻\", \"奇闻趣事\", \"组织内部管理\", \"海外民生\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"财经\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"宏观经济\", \"产业经济\", \"投资/创业\", \"公司新闻\", \"财经人物\", \"理财投资技巧\", \"股票\", \"期货\", \"银行\", \"基金\", \"保险\", \"外汇\", \"债券\", \"信托\", \"贵金属\", \"能源\", \"数字货币\", \"收藏\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"军事\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"武器装备\", \"军用飞行器\", \"军事历史\", \"军情/军事新闻\", \"军事知识\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"教育\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"学前教育\", \"小学教育\", \"初中教育\", \"高中教育\", \"高等教育\", \"语言学习\", \"留学相关\", \"职业考试\", \"技能教育\", \"演讲\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"体育\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"NBA\", \"CBA\", \"篮球其他\", \"国际足球\", \"国内足球\", \"足球其他\", \"综合体育\", \"冬季项目\", \"大众体育\", \"户外运动\", \"极限运动\", \"大型赛事\", \"搏击\", \"精英体育\", \"彩票\", \"棋牌\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"科技\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"科技股公司\", \"科技创投\", \"科技大佬\", \"互联网坊间八卦\", \"内容产业\", \"互联网金融\", \"互联网+\", \"通信\", \"区块链\", \"前沿产业(云/AR/VR等)\", \"信息技术\", \"电商\", \"软件工具\", \"数码\", \"汽车科技\", \"科技奇趣\", \"航空航天/机械\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"汽车\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"汽车产业\", \"新车资讯\", \"汽车评测\", \"汽车导购\", \"车展资讯\", \"二手车\", \"保养/开车技巧\", \"汽车改装\", \"汽车模型\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"科学\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"天文宇宙\", \"地理\", \"动植物世界\", \"医学\", \"生物考古\", \"科普\", \"科幻\", \"科学实验\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"房产\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"房产政策\", \"房价走势\", \"买房相关\", \"卖房相关\", \"租房相关\", \"海外房产\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"电影\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"电影资讯\", \"电影解说/片段\", \"电影混剪\", \"预告花絮\", \"微电影\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"电视剧\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"预告资讯\", \"精彩片段\", \"电视剧解说\", \"自制混剪\", \"恶搞配音\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"综艺\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"内地综艺\", \"港台综艺\", \"韩国综艺\", \"日本综艺\", \"欧美综艺\", \"晚会盛典\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"明星\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"八卦\", \"内地明星\", \"港台明星\", \"日本明星\", \"韩国明星\", \"欧美明星\", \"其他地区明星\", \"明星直发\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"音乐\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"流行\", \"电子\", \"轻音乐\", \"民谣\", \"说唱\", \"摇滚\", \"爵士\", \"R&amp;B\", \"布鲁斯\", \"古典\", \"网络歌曲\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"动漫\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"恋爱\", \"剧情\", \"日常\", \"恐怖\", \"犯罪\", \"悬疑\", \"武侠\", \"历史\", \"冒险\", \"动作\", \"竞技\", \"玄幻\", \"奇幻\", \"异能\", \"科幻\", \"原创短片\", \"盘点解读\", \"虚拟偶像\", \"声优资讯\", \"漫展资讯\", \"动漫壁纸\", \"cosplay\", \"鬼畜\", \"宅舞\", \"手绘\", \"MMD\", \"AMV/MAD/手书\", \"动漫音乐\", \"动漫相关资讯\", \"手办\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"游戏\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"手游\", \"小游戏\", \"主机游戏\", \"端游\", \"页游\", \"街机游戏\", \"便携游戏\", \"电竞赛事\", \"游戏展会\", \"游戏泛资讯\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"时尚\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"穿搭\", \"街拍\", \"美妆\", \"美体整形/医美\", \"时尚好物\", \"男士时尚\", \"时尚资讯/活动\", \"时尚大片\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"健康\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"养生保健\", \"健康科普知识\", \"心理健康\", \"饮食健康\", \"医疗\", \"药品\", \"疾病防治\", \"癌症\", \"口腔健康\", \"两性健康\", \"男性健康\", \"女性健康\", \"老人健康\", \"中医\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"旅游\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"自由行\", \"跟团游\", \"一日游/周末游\", \"亲子游\", \"定制游\", \"邮轮游\", \"穷游\", \"海岛\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"美食\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"探店攻略\", \"菜谱\", \"美食文化\", \"美食猎奇\", \"吃播\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"生活\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"生活百科\", \"生活方式\", \"日用家居\", \"购物信息\", \"生活记录\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"摄影\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"摄影作品\", \"摄影技术\", \"摄影器材\", \"后期处理\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"宠物\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"狗\", \"猫\", \"仓鼠\", \"兔子\", \"猪\", \"鱼\", \"鸟\", \"乌龟\", \"龙猫\", \"蛇\", \"蜥蜴\", \"蜘蛛\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"职场\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"职场经验\", \"求职面试\", \"商业管理\", \"创业指导\", \"网店经营\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"育儿\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"萌宝晒娃\", \"亲子成长\", \"备孕\", \"孕产\", \"产后\", \"饮食\", \"疾病防治\", \"护理发育\", \"母婴用品\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"情感\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"两性关系\", \"家庭关系\", \"单身\", \"人际关系\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"心灵鸡汤\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"人生感悟\", \"名人名言\", \"美文\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"星座命理\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"星座\", \"生肖\", \"命理\", \"风水\", \"相术\", \"血型\", \"塔罗牌\", \"测试\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"小说\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"东方玄幻\", \"都市小说\", \"仙侠武侠\", \"异界大陆\", \"异术超能\", \"西方奇幻\", \"同人文\", \"历史/历史架空\", \"游戏竞技\", \"科幻未来\", \"时空穿梭\", \"悬疑恐怖\", \"军事小说\", \"穿越架空\", \"豪门世家\", \"现代言情\", \"玄幻言情\", \"古代言情\", \"快穿小说\", \"宫闱宅斗\", \"仙侠奇缘\", \"异术超能\", \"种田文\", \"校园言情\", \"科幻神秘\", \"同人文\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"曲艺\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"相声\", \"小品\", \"评书\", \"京剧\", \"越剧\", \"秦腔\", \"昆曲\", \"豫剧\", \"粤剧\", \"潮剧\", \"黄梅戏\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"文化\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"文学\", \"绘画\", \"舞蹈\", \"音乐\", \"雕塑\", \"建筑\", \"戏剧\", \"工艺\", \"书法\", \"民俗\", \"哲学\", \"国学\", \"宗教\", \"收藏\", \"文物\", \"读书\", \"综合文化\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"历史\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"中国古代史\", \"中国近代史\", \"中国当代史\", \"党史\", \"世界史\", \"野史八卦\", \"历史考古\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"三农\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"农村\", \"农业\", \"农民\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"广告创意\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"创意广告\", \"公益广告\", \"品牌广告\", \"宣传短片\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"纪录片\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"美食纪录片\", \"自然纪录片\", \"科学纪录片\", \"历史纪录片\", \"旅游纪录片\", \"文化纪录片\", \"人物纪录片\", \"军事纪录片\", \"工业纪录片\", \"主旋律纪录片\", \"社会现实题材纪录片\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"搞笑\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"段子\", \"神回复/神操作\", \"糗事\", \"内涵图\", \"神剪辑\", \"整蛊/搞怪\", \"表演秀\", \"搞笑影视\", \"熊孩子\", \"爆笑动物\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"壁纸头像\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": [\"表情包\", \"套图\", \"壁纸\", \"头像\", \"文字图\", \"其他\"]</span></code><code><span class=\"code-snippet_outer\">    }, {</span></code><code><span class=\"code-snippet_outer\">        \"title\": \"其他\",</span></code><code><span class=\"code-snippet_outer\">        \"sublist\": []</span></code><code><span class=\"code-snippet_outer\">    }</span></code><code><span class=\"code-snippet_outer\">]</span></code><code><span class=\"code-snippet_outer\"><br></span></code></pre></section></div>"),
                Arrays.asList(article));
        System.out.println(page);
    }
    /**
     * 响应结果：
     * <pre>
     *     <code>
     *
     * {
     *   "appMsgId": 100000101,
     *   "available_auto_ad_count": [],
     *   "base_resp": {
     *     "err_msg": "",
     *     "ret": 0
     *   },
     *   "check_content_ad_resp": {
     *     "res": []
     *   },
     *   "data_seq": "2454755082467557377",
     *   "deleted_tagid": [],
     *   "filter_content_html": [{
     *     "content": "<p>试试<br  /></p>"
     *   }],
     *   "hint_word": [],
     *   "is_ad_optioal": 0,
     *   "msg": "",
     *   "price_option_list": [],
     *   "ret": "0"
     * }
     *     </code>
     * </pre>
     *
     * @param wxAccount
     * @param articles
     * @param csmArticles
     * @return
     */
    public static String postNewDraftPage(WxAccount wxAccount, List<String> articles,
                                          List<Article> csmArticles) {
        return postNewDraftPage(wxAccount, null, articles, csmArticles);
    }

    /**
     * 响应结果：
     * <pre>
     *     <code>
     *
     * {
     *   "appMsgId": 100000101,
     *   "available_auto_ad_count": [],
     *   "base_resp": {
     *     "err_msg": "",
     *     "ret": 0
     *   },
     *   "check_content_ad_resp": {
     *     "res": []
     *   },
     *   "data_seq": "2454755082467557377",
     *   "deleted_tagid": [],
     *   "filter_content_html": [{
     *     "content": "<p>试试<br  /></p>"
     *   }],
     *   "hint_word": [],
     *   "is_ad_optioal": 0,
     *   "msg": "",
     *   "price_option_list": [],
     *   "ret": "0"
     * }
     *     </code>
     * </pre>
     *
     * @param wxAccount
     * @param appMsgId    文章编号
     * @param articles
     * @param csmArticles
     * @return
     */
    public static String postNewDraftPage(WxAccount wxAccount, Long appMsgId, List<String> articles,
                                          List<Article> csmArticles) {
        boolean isUpdate = appMsgId != null;
        String referer = isUpdate ? DRAFT_INFO_URL.replace("${APPMSGID}", appMsgId.toString()) : NEW_DRAFT_PAGE_URL;
        String pageHtmlCode = WxHttpUtil.get(wxAccount, referer);
        boolean removed = pageHtmlCode.contains("此草稿已被删除") || pageHtmlCode.contains("此草稿已发表");
        if (removed) {
            isUpdate = false;
            referer = NEW_DRAFT_PAGE_URL;
            pageHtmlCode = WxHttpUtil.get(wxAccount, referer);
        }
        referer = referer.replace("${TOKEN}", wxAccount.getHtmlPageToken());
        String mpProfile = mpProfileCode(wxAccount);
        articles = articles.stream().map(article -> mpProfile + article).collect(Collectors.toList());
        Map<String, Object> body = createBody(wxAccount, articles, csmArticles, referer, pageHtmlCode);
        return postDraftPage(wxAccount, isUpdate, referer, body);
    }

    private static String postDraftPage(WxAccount wxAccount, boolean isUpdate, String referer, Map<String, Object> body) {
        return WxHttpUtil.postForm(wxAccount,
                NEW_DRAFT_SUBMIT_URL.replace("${SUB_TYPE}", isUpdate ? "update" : "create"),
                body, req -> req.header(Header.REFERER, referer));
    }

    private static Map<String, Object> createBody(WxAccount wxAccount, List<String> articles,
                                                  List<Article> csmArticles, String referer, String pageHtmlCode) {
        String infos = findInfosFromHtml(pageHtmlCode);
        DraftInfos draftInfos = DraftInfos.from(infos);
        List<NewsItem> newsItems = draftInfos.getItem();
        if (newsItems.isEmpty()) {
            newsItems.add(new NewsItem());
        }
        NewsItem newsItem = newsItems.get(0);
        List<MultiItem> items = Optional.ofNullable(newsItem.getMultiItem()).orElseGet(() -> Arrays.asList(new MultiItem()));
        MapBuilder<String, Object> builder = MapUtil.builder(new HashMap<>(16 + articles.size() * 82));
        builder.put("token", wxAccount.getHtmlPageToken())
                .put("lang", "zh_CN")
                .put("f", "json")
                .put("ajax", 1)
                .put("operate_from", "Chrome")
                .put("remind_flag", null)
                .put("isnew", 0)
                .put("count", articles.size())
                .put("is_auto_type_setting", 0)
                .put("is_auto_save", 0)
                .put("isneedsave", 0)
                .put("AppMsgId", newsItem.getAppId())
                .put("data_seq", Optional.ofNullable(newsItem.getDataSeq()).orElse("0"))
                .put("articlenum", articles.size())
                .put("random", random());
        String author = wxAccount.getName();
        String pageInfo = WxHttpUtil.get(wxAccount, WRITER_MGR_URL.replace("${RANDOM}", String.valueOf(random()))
                .replace("${AUTHOR}", author));
        JSONObject writerInfo = (JSONObject) JSON.parseObject(pageInfo).getJSONObject("pageinfo")
                .getJSONArray("writerlist").get(0);
        for (int i = 0; i < articles.size(); i++) {
            String content = articles.get(i);
            Article article = csmArticles.get(i);
            MultiItem item = Optional.ofNullable(items.get(i)).orElseGet(MultiItem::new);
            builder.put("ad_id" + i, "")
                    .put("ad_video_transition" + i, "")
                    .put("applyori" + i, 0)
                    .put("appmsg_album_info" + i, // 合集的信息，每篇文章最多添加5个合集
                            GSON_INSTANCE.toJson(Optional.ofNullable(item.getAppmsgAlbumInfo()).orElseGet(() -> {
                                AppmsgAlbumInfo appmsgAlbumInfo = new AppmsgAlbumInfo();
                                appmsgAlbumInfo.setAppmsgAlbumInfos(new ArrayList<>());
                                return appmsgAlbumInfo;
                            }))
                    )
                    .put("audio_info" + i, "{\"audio_infos\":[]}")
                    .put("author" + i, Optional.ofNullable(item.getAuthor()).orElse(wxAccount.getName()))
                    .put("auto_gen_digest" + i, 0) //自动生成摘要？
                    .put("can_insert_ad" + i, 1) //是否可以插入广告
                    .put("cardid" + i, "")
                    .put("cardlimit" + i, "")
                    .put("cardquantity" + i, "")
                    .put("categories_list" + i,
                            GSON_INSTANCE.toJson(Optional.ofNullable(item.getCategoriesList())
                                    .orElseGet(ArrayList::new))
                    )
                    .put("compose_info" + i, "")
                    .put("content" + i, content)
                    .put("copyright_img_list" + i, "")
                    .put("copyright_type" + i, 1)
                    .put("digest" + i,
                            Optional.ofNullable(item.getDigest()).orElseGet(() -> {
                                String summary = article.getSummary();
                                String digest = CharSequenceUtil.isNotEmpty(summary) ? summary : HtmlUtil.cleanHtmlTag(content).trim();
                                return digest.substring(0, Math.min(digest.length(), 100));
                            })
                    )
                    .put("dot" + i, "{}")
                    .put("fee" + i, 0)
                    .put("fileid" + i, "")
                    .put("finder_draft_id" + i, 0)
                    .put("free_content" + i, "")
                    .put("guide_words" + i, "")
                    .put("hit_nickname" + i, "")
                    .put("insert_ad_mode" + i, "")
                    .put("is_cartoon_copyright" + i, 0)
                    .put("is_finder_video" + i, 0)
                    .put("is_pay_subscribe" + i, 0)
                    .put("is_set_sync_to_finder" + i, 0)
                    .put("is_share_copyright" + i, 0)
                    .put("is_video_recommend" + i, 0)
                    .put("last_choose_cover_from" + i, 1)
                    .put("music_id" + i, "")
                    .put("need_open_comment" + i, 0)
                    .put("only_fans_can_comment" + i, 0)
                    .put("only_fans_days_can_comment" + i, 0)
                    .put("open_fansmsg" + i, 1);
            String category = Optional.ofNullable(article.getCategory())
                    .orElse("其他");
            if (!category.equals("其他")) {
                category = category + "_" + Optional.ofNullable(article.getSubCategory()).orElse("其他");
            }
            builder.put("pay_album_info" + i, "")
                    .put("pay_desc" + i, "")
                    .put("pay_fee" + i, "")
                    .put("pay_gifts_count" + i, 0)
                    .put("pay_preview_percent" + i, "")
                    .put("platform" + i, "")
                    .put("related_video" + i, "")
                    .put("releasefirst" + i, "")
                    .put("reply_flag" + i, 0)
                    .put("reprint_recommend_content" + i, "")
                    .put("reprint_recommend_title" + i, "")
                    .put("reward_reply_id" + i, "")
                    .put("share_copyright_url" + i, "")
                    .put("share_imageinfo" + i, "{\"list\":[]}")
                    .put("share_page_type" + i, 0)
                    .put("share_video_id" + i, "")
                    .put("share_voice_id" + i, "")
                    .put("shortvideofileid" + i, "")
                    .put("show_cover_pic" + i, 0)
                    .put("source_article_type" + i, "")
                    .put("sourceurl" + i, Optional.ofNullable(item.getSourceUrl()).orElse(article.getTargetLink()))
                    .put("supervoteid" + i, "")
                    .put("sync_to_finder_cover" + i, "")
                    .put("sync_to_finder_cover_source" + i, "")
                    .put("title" + i, Optional.ofNullable(item.getTitle()).orElse(article.getTitle()))
                    .put("video_id" + i, "")
                    .put("video_ori_status" + i, "")
                    .put("vid_type" + i, "")
                    .put("voteid" + i, "")
                    .put("voteismlt" + i, "");
            builder.putAll(initAppreciationAccount(i, category, writerInfo));
            if (CharSequenceUtil.isEmpty(item.getCdnUrlBack())) {
                cdnFromContentImg(wxAccount, content, i, "生活", builder, referer);
            } else {
                builder.put("cdn_url" + i, item.getCdnUrl())
                        .put("cdn_235_1_url" + i, item.getCdn2351Url())
                        .put("cdn_16_9_url" + i, item.getCdn169Url())
                        .put("cdn_1_1_url" + i, item.getCdn11Url())
                        .put("cdn_url_back" + i, item.getCdnUrlBack());
                String crop_list = "[]";
                builder.put("crop_list", "{\"crop_list\":" + crop_list + "}");
            }
        }
        return builder.build();
    }

    /**
     * 返回原创声明的相关信息
     *
     * @param index      当前文章索引
     * @param category   当前文章分类
     * @param writerInfo 作者信息
     * @return
     */
    private static Map<String, Object> initAppreciationAccount(int index, String category,
                                                               JSONObject writerInfo) {
        return MapBuilder.create(new HashMap<String, Object>())
                .put("can_reward" + index, writerInfo.getInteger("can_reward"))
                .put("copyright_type" + index, 1)
                .put("reprint_permit_type" + index, 1)
                .put("allow_reprint" + index, 0)
                .put("allow_reprint_modify" + index, 0)
                .put("original_article_type" + index, category)
                .put("writerid" + index, writerInfo.getString("writerid"))
                .put("author" + index, writerInfo.getString("nickname"))
                .put("ori_white_list" + index, "{\"white_list\":[]}").build();

    }

    private static String findInfosFromHtml(String pageHtmlCode) {
        return Arrays.stream(pageHtmlCode.split("\\n"))
                .filter(s -> s.trim().startsWith("infos") && s.trim().endsWith(","))
                .findFirst()
                .map(s -> {
                    String result = s.trim().replaceFirst("infos = ", "");
                    return result.substring(0, result.lastIndexOf(","));
                }).orElse("{}");
    }


    private static void cdnFromContentImg(WxAccount wxAccount, String articles, int index, String query, MapBuilder<String,
            Object> builder, String referer) {
        String img = Jsoup.parse(articles).getElementsByTag("img").attr("data-src");
        if (CharSequenceUtil.isEmpty(img)) {
            JSONArray result = JSON.parseObject(photoGallery(wxAccount, query)).getJSONArray("result");
            JSONObject photo = (JSONObject) result.get(RandomUtil.randomInt(0, result.size()));
            img = photo.getJSONObject("photo").getString("url");
            img = WxHttpUtil.BASE_URL + "/photogalleryproxy?action=proxy&url=" + URLUtil.encode(img) + "&supplier=5" +
                    "&from_public_pic=1";
        }

        double size0_x1 = 0;
        double size0_y1 = 0.1810810810810811;
        double size0_x2 = 1;
        double size0_y2 = 0.8182288671650374;
        double size1_x1 = 0;
        double size1_y1 = 0.1810810810810811;
        double size1_x2 = 1;
        double size1_y2 = 0.8182288671650374;
        Map<String, Object> build = MapUtil.builder(new HashMap<String, Object>())
                .put("imgurl", img)
                .put("size_count", 2)
                .put("size0_x1", size0_x1).put("size0_y1", size0_y1)
                .put("size0_x2", size0_x2).put("size0_y2", size0_y2)
                .put("size1_x1", size1_x1).put("size1_y1", size1_y1)
                .put("size1_x2", size1_x2).put("size1_y2", size1_y2)
                .put("token", wxAccount.getHtmlPageToken())
                .put("lang", "zh_CN")
                .put("f", "json")
                .put("ajax", 1).build();
        /**
         * {
         *   "base_resp": {
         *     "err_msg": "ok",
         *     "ret": 0
         *   },
         *   "result": [{
         *     "cdnurl": "http://mmbiz.qpic.cn/mmbiz_jpg/jIiaBrXsRIjEvEOwUHaTAKkgJNS5ZVvic4fxUQpGPYnAogrqDic7poibRmMj1eVg2nibKdoxMubGLSzia1plTicdialaeg/0?wx_fmt=jpeg",
         *     "file_id": 100000102
         *   }, {
         *     "cdnurl": "http://mmbiz.qpic.cn/mmbiz_jpg/jIiaBrXsRIjEvEOwUHaTAKkgJNS5ZVvic4FDFvV9gqmDHJLS42C63JibicY07CBNrJ6NH2Ihfvavy7AMeAAgqzos2A/0?wx_fmt=jpeg",
         *     "file_id": 100000103
         *   }]
         * }
         */
        JSONObject object = JSON.parseObject(WxHttpUtil.request(wxAccount, CROP_MULTI, Method.POST, req -> {
            req.form(build);
            req.header(Header.REFERER, referer);
            req.contentType(ContentType.FORM_URLENCODED.getValue());
        }));
        JSONArray array = object.getJSONArray("result");
        String cdnUrl0 = ((JSONObject) array.get(0)).getString("cdnurl");
        File file = new File("0.jpg");
        HttpUtil.downloadFile(cdnUrl0, file);
        BufferedImage image = ImgUtil.read(file);
        int picWitdh = image.getWidth();
        int picHeight = image.getHeight();
        FileUtil.del(file);
        builder.put("cdn_url" + index, cdnUrl0)
                .put("cdn_235_1_url" + index, cdnUrl0)
                .put("cdn_16_9_url" + index, cdnUrl0)
                .put("cdn_1_1_url" + index, ((JSONObject) array.get(1)).getString("cdnurl"))
                .put("cdn_url_back" + index, img);
        String crop_list = JSON.toJSONString(Arrays.asList(
                MapUtil.builder().put("ratio", "2.35_1")
                        .put("x1", (int) (size0_x1 * picWitdh)).put("y1", (int) (size0_y1 * picHeight))
                        .put("x2", (int) (size0_x2 * picWitdh)).put("y2", (int) (size0_y2 * picHeight)).build(),
                MapUtil.builder().put("ratio", "1_1")
                        .put("x1", (int) (size1_x1 * picWitdh)).put("y1", (int) (size1_y1 * picHeight))
                        .put("x2", (int) (size1_x2 * picWitdh)).put("y2", (int) (size1_y2 * picHeight)).build()
        ));
        builder.put("crop_list" + index, "{\"crop_list\":" + crop_list + "}");
    }

    /**
     * <p>返回结果：</p>
     * <pre>
     *     <code>
     *
     * {
     *     "base_resp": {
     *         "err_msg": "ok",
     *         "ret": 0
     *     },
     *     "history_list": [{
     *             "query": "XXXX"
     *         }, {
     *             "query": "日记"
     *         }, {
     *             "query": "学习"
     *         }
     *     ],
     *     "need_continue": 1,
     *     "result": [{
     *             "is_favorite": 0,
     *             "photo": {
     *                 "medium_url": "https://inews.gtimg.com/newsapp_bt/0/5002a6euqqgagke5/640?appid=764c87a2b97faf28",
     *                 "supplier": 5,
     *                 "type": 1,
     *                 "unique_id": "138954472658329600",
     *                 "url": "https://inews.gtimg.com/newsapp_bt/0/5002a6euqqgagke5/0?appid=764c87a2b97faf28"
     *             },
     *             "seq": 1
     *         }, {
     *             "is_favorite": 0,
     *             "photo": {
     *                 "medium_url": "https://inews.gtimg.com/newsapp_bt/0/5002a6etul3qgkan/640?appid=764c87a2b97faf28",
     *                 "supplier": 5,
     *                 "type": 1,
     *                 "unique_id": "132923971501883392",
     *                 "url": "https://inews.gtimg.com/newsapp_bt/0/5002a6etul3qgkan/0?appid=764c87a2b97faf28"
     *             },
     *             "seq": 2
     *         }
     *     ]
     * }
     *     </code>
     * </pre>
     *
     * @param wxAccount
     * @param query
     * @return
     */
    public static String photoGallery(WxAccount wxAccount, String query) {
        return WxHttpUtil.get(wxAccount, PHOTO_GALLERY.replace("${QUERY}", query));
    }

    /**
     * 0到1范围内的随机数，小数位为16位
     *
     * @return 小数位为16位的随机数
     */
    private static double random() {
        return RandomUtil.randomDouble(1, 16, RoundingMode.HALF_UP);
    }

}
