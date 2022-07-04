
package com.github.niefy.modules.wx.form.draft;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.github.niefy.common.utils.WxMpHtmlPageApiUtil.GSON_INSTANCE;


/**
 * @author cuitpanfei
 */
public class DraftInfos {


    @SerializedName("file_cnt")
    private FileCnt fileCnt;
    @SerializedName("is_upload_cdn_ok")
    private Long isUploadCdnOk;
    @Expose
    private List<NewsItem> item;
    @SerializedName("material_status")
    private Long materialStatus;
    @SerializedName("search_cnt")
    private Long searchCnt;
    @SerializedName("search_id")
    private String searchId;

    public FileCnt getFileCnt() {
        return fileCnt;
    }

    public void setFileCnt(FileCnt fileCnt) {
        this.fileCnt = fileCnt;
    }

    public Long getIsUploadCdnOk() {
        return isUploadCdnOk;
    }

    public void setIsUploadCdnOk(Long isUploadCdnOk) {
        this.isUploadCdnOk = isUploadCdnOk;
    }

    public List<NewsItem> getItem() {
        return item;
    }

    public void setItem(List<NewsItem> item) {
        this.item = item;
    }

    public Long getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(Long materialStatus) {
        this.materialStatus = materialStatus;
    }

    public Long getSearchCnt() {
        return searchCnt;
    }

    public void setSearchCnt(Long searchCnt) {
        this.searchCnt = searchCnt;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public static DraftInfos from(String infos) {
        return GSON_INSTANCE.fromJson(infos, DraftInfos.class);
    }

    @Override
    public String toString() {
        return GSON_INSTANCE.toJson(this);
    }
}
