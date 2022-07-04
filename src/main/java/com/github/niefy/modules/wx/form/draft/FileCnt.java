
package com.github.niefy.modules.wx.form.draft;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FileCnt {

    @SerializedName("app_msg_cnt")
    private Long appMsgCnt;
    @SerializedName("app_msg_sent_cnt")
    private Long appMsgSentCnt;
    @SerializedName("appmsg_template_cnt")
    private Long appmsgTemplateCnt;
    @SerializedName("commondity_msg_cnt")
    private Long commondityMsgCnt;
    @SerializedName("draft_count")
    private Long draftCount;
    @SerializedName("img_cnt")
    private Long imgCnt;
    @SerializedName("mediaapi_appmsg_cnt")
    private Long mediaapiAppmsgCnt;
    @SerializedName("short_video_cnt")
    private Long shortVideoCnt;
    @Expose
    private Long total;
    @SerializedName("video_cnt")
    private Long videoCnt;
    @SerializedName("video_msg_cnt")
    private Long videoMsgCnt;
    @SerializedName("voice_cnt")
    private Long voiceCnt;

    public Long getAppMsgCnt() {
        return appMsgCnt;
    }

    public void setAppMsgCnt(Long appMsgCnt) {
        this.appMsgCnt = appMsgCnt;
    }

    public Long getAppMsgSentCnt() {
        return appMsgSentCnt;
    }

    public void setAppMsgSentCnt(Long appMsgSentCnt) {
        this.appMsgSentCnt = appMsgSentCnt;
    }

    public Long getAppmsgTemplateCnt() {
        return appmsgTemplateCnt;
    }

    public void setAppmsgTemplateCnt(Long appmsgTemplateCnt) {
        this.appmsgTemplateCnt = appmsgTemplateCnt;
    }

    public Long getCommondityMsgCnt() {
        return commondityMsgCnt;
    }

    public void setCommondityMsgCnt(Long commondityMsgCnt) {
        this.commondityMsgCnt = commondityMsgCnt;
    }

    public Long getDraftCount() {
        return draftCount;
    }

    public void setDraftCount(Long draftCount) {
        this.draftCount = draftCount;
    }

    public Long getImgCnt() {
        return imgCnt;
    }

    public void setImgCnt(Long imgCnt) {
        this.imgCnt = imgCnt;
    }

    public Long getMediaapiAppmsgCnt() {
        return mediaapiAppmsgCnt;
    }

    public void setMediaapiAppmsgCnt(Long mediaapiAppmsgCnt) {
        this.mediaapiAppmsgCnt = mediaapiAppmsgCnt;
    }

    public Long getShortVideoCnt() {
        return shortVideoCnt;
    }

    public void setShortVideoCnt(Long shortVideoCnt) {
        this.shortVideoCnt = shortVideoCnt;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getVideoCnt() {
        return videoCnt;
    }

    public void setVideoCnt(Long videoCnt) {
        this.videoCnt = videoCnt;
    }

    public Long getVideoMsgCnt() {
        return videoMsgCnt;
    }

    public void setVideoMsgCnt(Long videoMsgCnt) {
        this.videoMsgCnt = videoMsgCnt;
    }

    public Long getVoiceCnt() {
        return voiceCnt;
    }

    public void setVoiceCnt(Long voiceCnt) {
        this.voiceCnt = voiceCnt;
    }

}
