package com.github.niefy.common.utils;

/**
 * @author cuitpanfei
 */
public class WxHtmlArticleRetErrCode {
    public static String msg(int ret) {
        String errmsg;
        switch (ret) {
            case -8:
            case -6:
                errmsg = "请输入验证码";
                break;
            case 62752:
                errmsg = "可能含有具备安全风险的链接，请检查";
                break;
            case 64505:
                errmsg = "发送预览失败，请稍后再试";
                break;
            case 64504:
                errmsg = "保存图文消息发送错误，请稍后再试";
                break;
            case 64518:
                errmsg = "正文只能包含一个投票";
                break;
            case 10704:
            case 10705:
                errmsg = "该素材已被删除";
                break;
            case 10701:
                errmsg = "用户已被加入黑名单，无法向其发送消息";
                break;
            case 10703:
                errmsg = "对方关闭了接收消息";
                break;
            case 10700:
            case 64503:
                errmsg = "1.接收预览消息的微信尚未关注公众号，请先扫码关注<br /> 2.如果已经关注公众号，请查看微信的隐私设置（在手机微信的“我->设置->隐私->添加我的方式”中），并开启“可通过以下方式找到我”的“手机号”、“微信号”、“QQ号”，否则可能接收不到预览消息";
                break;
            case 64502:
                errmsg = "你输入的微信号不存在，请重新输入";
                break;
            case 64501:
                errmsg = "你输入的帐号不存在，请重新输入";
                break;
            case 412:
                errmsg = "图文中含非法外链";
                break;
            case 64515:
                errmsg = "当前素材非最新内容，请重新打开并编辑";
                break;
            case 320001:
                errmsg = "该素材已被删除，无法保存";
                break;
            case 64702:
                errmsg = "标题超出64字长度限制";
                break;
            case 64703:
                errmsg = "摘要超出120字长度限制";
                break;
            case 64704:
            case 64708:
                errmsg = "推荐语超出长度限制";
                break;
            case 200041:
                errmsg = "此素材有文章存在违规，无法编辑";
                break;
            case 64506:
                errmsg = "保存失败,链接不合法";
                break;
            case 64507:
                errmsg = "内容不能包含外部链接，请输入http://或https://开头的公众号相关链接";
                break;
            case 64510:
                errmsg = "内容不能包含音频，请调整";
                break;
            case 64511:
                errmsg = "内容不能包多个音频，请调整";
                break;
            case 64512:
                errmsg = "文章中音频错误,请使用音频添加按钮重新添加。";
                break;
            case 64508:
                errmsg = "查看原文链接可能具备安全风险，请检查";
                break;
            case 64550:
                errmsg = "请勿插入不合法的图文消息链接";
                break;
            case 64563:
                errmsg = "合集链接格式错误，请确认后重试";
                break;
            case 64558:
                errmsg = "请勿插入图文消息临时链接，链接会在短期失效";
                break;
            case 64559:
                errmsg = "不支持添加未群发的文章/视频/图片/语音链接";
                break;
            case -99:
            case 64705:
                errmsg = "内容超出字数，请调整";
                break;
            case -1:
                errmsg = "系统错误，请注意备份内容后重试";
                break;
            case -2:
            case 200002:
                errmsg = "参数错误，请注意备份内容后重试";
                break;
            case 64509:
                errmsg = "正文中不能包含超过3个视频，请重新编辑正文后再保存。";
                break;
            case -5:
                errmsg = "服务错误，请注意备份内容后重试。";
                break;
            case 64513:
                errmsg = "请从正文中选择封面，再尝试保存。";
                break;
            case 64714:
                errmsg = "所选的部分合集已被删除，请重新选择。";
                break;
            case -206:
                errmsg = "目前，服务负荷过大，请稍后重试。";
                break;
            case 10801:
                errmsg = "标题不能有违反公众平台协议、相关法律法规和政策的内容，请重新编辑。";
                break;
            case 10802:
                errmsg = "作者不能有违反公众平台协议、相关法律法规和政策的内容，请重新编辑。";
                break;
            case 10803:
                errmsg = "敏感链接，请重新添加。";
                break;
            case 10804:
                errmsg = "摘要不能有违反公众平台协议、相关法律法规和政策的内容，请重新编辑。";
                break;
            case 10806:
            case 10808:
                errmsg = "内容不能有违反公众平台协议、相关法律法规和政策的内容，请重新编辑。";
                break;
            case 10807:
                errmsg = "内容不能违反公众平台协议、相关法律法规和政策，请重新编辑。";
                break;
            case 200003:
                errmsg = "登录态超时，请重新登录。";
                break;
            case 64551:
                errmsg = "请检查图文消息中的微视链接后重试。";
                break;
            case 64552:
                errmsg = "请检查阅读原文中的链接后重试。";
                break;
            case 64553:
                errmsg = "请不要在图文消息中插入超过5张卡券。请删减卡券后重试。";
                break;
            case 64554:
                errmsg = "在当前情况下不允许在图文消息中插入卡券，请删除卡券后重试。";
                break;
            case 64555:
                errmsg = "请检查图文消息卡片跳转的链接后重试。";
                break;
            case 64556:
                errmsg = "卡券不属于该公众号，请删除后重试";
                break;
            case 64557:
                errmsg = "公众号已不再支持下发优惠券，请删除后重试。";
                break;
            case 13002:
                errmsg = "该广告卡片已过期，删除后才可保存成功";
                break;
            case 13003:
                errmsg = "已有文章插入过该广告卡片，一个广告卡片仅可插入一篇文章";
                break;
            case 13004:
                errmsg = "该广告卡片与图文消息位置不一致";
                break;
            case 15801:
            case 15802:
            case 15803:
            case 15804:
            case 15805:
            case 15806:
                errmsg = "你所编辑的内容可能含有违反微信公众平台平台协议、相关法律法规和政策的内容";
                break;
            case 1530503:
            case 1530504:
                errmsg = "请勿添加其他公众号的主页链接";
                break;
            case 1530510:
                errmsg = "链接已失效，请在手机端重新复制链接";
                break;
            case 153007:
            case 153008:
            case 153009:
            case 153010:
                errmsg = "很抱歉，原创声明不成功|你的文章内容未达到声明原创的要求：<br />文章文字字数（不包含标点符号和空格）大于300字，且自己创作的内容大于引用内容。<br />说明：上述要求中，文章文字字数不包含标点符号和空格，请知悉。";
                break;
            case 153200:
                errmsg = "无权限声明原创，取消声明后重试";
                break;
            case 1530511:
                errmsg = "链接已失效，请在手机端重新复制链接";
                break;
            case 220001:
                errmsg = "\"素材管理\"中的存储数量已达到上限，请删除后再操作。";
                break;
            case 220002:
                errmsg = "你的图片库已达到存储上限，请进行清理。";
                break;
            case 153012:
                errmsg = "请设置转载类型";
                break;
            case 200042:
                errmsg = "图文中包含的小程序素材不能多于50个、小程序帐号不能多于10个";
                break;
            case 200043:
                errmsg = "图文中包含没有关联的小程序，请删除后再保存";
                break;
            case 64601:
                errmsg = "一篇文章只能插入一个广告卡片";
                break;
            case 64602:
                errmsg = "尚未开通文中广告位，但文章中有广告";
                break;
            case 64603:
                errmsg = "文中广告前不足300字";
                break;
            case 64604:
                errmsg = "文中广告后不足300字";
                break;
            case 64605:
                errmsg = "文中不能同时插入文中广告和互选广告";
                break;
            case 64607:
                errmsg = "付费图文不可插入广告，请将广告移除后再保存";
                break;
            case 64608:
                errmsg = "一篇文章最多添加一个搜索组件";
                break;
            case 65101:
                errmsg = "图文模版数量已达到上限，请删除后再操作";
                break;
            case 64560:
                errmsg = "请勿插入历史图文消息页链接";
                break;
            case 64561:
                errmsg = "请勿插入mp.weixin.qq.com域名下的非图文消息链接";
                break;
            case 64562:
                errmsg = "请勿插入非mp.weixin.qq.com域名的链接";
                break;
            case 153013:
                errmsg = "文章内含有投票，不能设置为开放转载";
                break;
            case 153014:
                errmsg = "文章内含有卡券，不能设置为开放转载";
                break;
            case 153015:
            case 153016:
                errmsg = "文章内含有小程序链接，不能设置为开放转载";
                break;
            case 153017:
                errmsg = "文章内含有小程序卡片，不能设置为开放转载";
                break;
            case 153018:
                errmsg = "文章内含有商品，不能设置为开放转载";
                break;
            case 153019:
            case 153020:
            case 153021:
                errmsg = "文章内含有广告卡片，不能设置为开放转载";
                break;
            case 153101:
                errmsg = "含有原文已删除的转载文章，请删除后重试";
                break;
            case 64707:
                errmsg = "赞赏账户设置失效";
                break;
            case 67030:
                errmsg = "赞赏功能不可用";
                break;
            case 67028:
                errmsg = "非原创视频落地页不能开赞赏";
                break;
            case 64710:
                errmsg = "原创功能已被封禁不可设置付费图文，请切换为普通图文";
                break;
            case 64717:
                errmsg = "你所转载的原创文章已被删除，请备份内容后重试";
                break;
            case 202605:
                errmsg = "付费功能已被封禁不可设置付费图文，请切换为普通图文";
                break;
            case 420001:
                errmsg = "封面图不支持GIF，请更换";
                break;
            case 353004:
                errmsg = "不支持添加商品，请删除后重试";
                break;
            case 442001:
                errmsg = "帐号新建/编辑素材能力已被封禁，暂不可使用。";
                break;
            case 404002:
                errmsg = "每个搜索组件最多添加6个搜索关键词";
                break;
            case 404003:
                errmsg = "每个搜索关键词最多16个字符";
                break;
            case 240021:
                errmsg = "文章中含有失效的地理位置，请删除或修改位置";
                break;
            case 64521:
                errmsg = "小商店商品卡片超过上限";
                break;
            case 64609:
                errmsg = "包含了不合法的小商店商品卡片";
                break;
            case 342002:
                errmsg = "含有未上架的小商店商品";
                break;
            case 342003:
                errmsg = "含有非公众号关联商店的商品";
                break;
            case 67032:
                errmsg = "含有审核失败的音频，请保存后删除或替换素材";
                break;
            case 67033:
                errmsg = "含有审核中的音频，请保存后等待审核通过再重试";
                break;
            case 67034:
                errmsg = "含有转码失败的音频，请保存后删除或替换素材";
                break;
            case 67035:
                errmsg = "含有转码中的音频，请保存后等待转码和审核通过再重试";
                break;
            case 67037:
                errmsg = "含有已下架的音频，请保存后删除或替换素材";
                break;
            case 404001:
            case 404004:
            case 404005:
            default:
                errmsg = "系统繁忙，请稍后重试";
        }
        return errmsg;
    }
}
