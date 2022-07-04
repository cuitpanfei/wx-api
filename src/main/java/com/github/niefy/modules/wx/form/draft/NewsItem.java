
package com.github.niefy.modules.wx.form.draft;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class NewsItem {

    @SerializedName("app_id")
    private Long appId;
    @Expose
    private String author;
    @SerializedName("author_status")
    private Long authorStatus;
    @SerializedName("author_username")
    private String authorUsername;
    @Expose
    private Long authority;
    @SerializedName("can_open_reward")
    private Long canOpenReward;
    @SerializedName("copied_appmsg_infos")
    private List<Object> copiedAppmsgInfos;
    @SerializedName("create_time")
    private String createTime;
    @SerializedName("data_seq")
    private String dataSeq;
    @Expose
    private String digest;
    @SerializedName("file_id")
    private Long fileId;
    @SerializedName("has_cps_product")
    private Long hasCpsProduct;
    @SerializedName("img_url")
    private String imgUrl;
    @SerializedName("is_auto_type_setting")
    private Long isAutoTypeSetting;
    @SerializedName("is_illegal")
    private Long isIllegal;
    @SerializedName("is_sync_top_stories")
    private Long isSyncTopStories;
    @SerializedName("multi_item")
    private List<MultiItem> multiItem;
    @SerializedName("pay_subscribe_desc")
    private String paySubscribeDesc;
    @SerializedName("publish_time")
    private Long publishTime;
    @Expose
    private Long seq;
    @SerializedName("show_cover_pic")
    private Long showCoverPic;
    @Expose
    private String title;
    @SerializedName("update_time")
    private String updateTime;
    @Expose
    private Long writerid;

}
