
package com.github.niefy.modules.wx.form.draft;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class MultiItem {

    @SerializedName("allow_reprint")
    private Long allowReprint;
    @SerializedName("allow_reprint_modify")
    private Long allowReprintModify;
    @SerializedName("appmsg_album_info")
    private AppmsgAlbumInfo appmsgAlbumInfo;
    @SerializedName("appmsg_danmu_pub_type")
    private List<Object> appmsgDanmuPubType;
    @Expose
    private String author;
    @SerializedName("author_status")
    private Long authorStatus;
    @SerializedName("author_username")
    private String authorUsername;
    @Expose
    private Long authority;
    @SerializedName("auto_elect_flag")
    private Long autoElectFlag;
    @SerializedName("auto_elect_groups")
    private String autoElectGroups;
    @SerializedName("auto_gen_digest")
    private Long autoGenDigest;
    @SerializedName("can_insert_ad")
    private Long canInsertAd;
    @SerializedName("can_open_reward")
    private Long canOpenReward;
    @SerializedName("can_reward")
    private Long canReward;
    @SerializedName("categories_list")
    private List<Object> categoriesList;
    @SerializedName("cdn_1_1_url")
    private String cdn11Url;
    @SerializedName("cdn_16_9_url")
    private String cdn169Url;
    @SerializedName("cdn_235_1_url")
    private String cdn2351Url;
    @SerializedName("cdn_url")
    private String cdnUrl;
    @SerializedName("cdn_url_back")
    private String cdnUrlBack;
    @SerializedName("copyright_type")
    private Long copyrightType;
    @Expose
    private String cover;
    @SerializedName("cover_type")
    private Long coverType;
    @SerializedName("del_flag")
    private Long delFlag;
    @Expose
    private String digest;
    @SerializedName("file_id")
    private Long fileId;
    @SerializedName("free_content")
    private String freeContent;
    @SerializedName("free_publish_status")
    private String freePublishStatus;
    @SerializedName("has_red_packet_cover")
    private Long hasRedPacketCover;
    @SerializedName("insert_ad_mode")
    private Long insertAdMode;
    @SerializedName("is_cartoon_copyright")
    private Long isCartoonCopyright;
    @SerializedName("is_mp_video")
    private Long isMpVideo;
    @SerializedName("is_new_video")
    private Long isNewVideo;
    @SerializedName("is_original")
    private Long isOriginal;
    @SerializedName("is_pay_subscribe")
    private Long isPaySubscribe;
    @SerializedName("is_video_recommend")
    private Long isVideoRecommend;
    @SerializedName("mediaapi_publish_status")
    private Long mediaapiPublishStatus;
    @SerializedName("more_read_info")
    private MoreReadInfo moreReadInfo;
    @SerializedName("need_open_comment")
    private Long needOpenComment;
    @SerializedName("only_fans_can_comment")
    private Boolean onlyFansCanComment;
    @SerializedName("only_fans_days_can_comment")
    private Boolean onlyFansDaysCanComment;
    @SerializedName("open_fansmsg")
    private Long openFansmsg;
    @SerializedName("ori_white_list")
    private String oriWhiteList;
    @SerializedName("original_article_type")
    private String originalArticleType;
    @SerializedName("pay_desc")
    private String payDesc;
    @SerializedName("pay_feconfig")
    private String payFeconfig;
    @SerializedName("pay_fee")
    private Long payFee;
    @SerializedName("pay_gifts_count")
    private Long payGiftsCount;
    @SerializedName("pay_preview_percent")
    private Long payPreviewPercent;
    @SerializedName("pay_subscribe_desc")
    private String paySubscribeDesc;
    @Expose
    private String platform;
    @SerializedName("publish_info")
    private String publishInfo;
    @SerializedName("related_video")
    private List<Object> relatedVideo;
    @Expose
    private Long releasefirst;
    @Expose
    private Long releasetime;
    @SerializedName("reply_flag")
    private Long replyFlag;
    @SerializedName("reprint_permit_type")
    private Long reprintPermitType;
    @SerializedName("reward_money")
    private Long rewardMoney;
    @SerializedName("reward_wording")
    private String rewardWording;
    @Expose
    private Long seq;
    @SerializedName("share_imageinfo")
    private List<Object> shareImageinfo;
    @SerializedName("share_page_type")
    private Long sharePageType;
    @SerializedName("share_videoinfo")
    private List<Object> shareVideoinfo;
    @SerializedName("share_voiceinfo")
    private List<Object> shareVoiceinfo;
    @SerializedName("short_content")
    private ShortContent shortContent;
    @SerializedName("show_cover_pic")
    private Long showCoverPic;
    @SerializedName("smart_product")
    private Long smartProduct;
    @SerializedName("source_url")
    private String sourceUrl;
    @SerializedName("tag_approved")
    private Boolean tagApproved;
    @Expose
    private List<Object> tagid;
    @Expose
    private List<Object> tags;
    @Expose
    private String title;
    @SerializedName("video_desc")
    private String videoDesc;
    @SerializedName("video_ori_list")
    private List<Object> videoOriList;
    @SerializedName("wecoin_count")
    private Long wecoinCount;
    @Expose
    private Long writerid;

}
