package com.github.niefy.modules.wx.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * 自动回复规则
 *
 * @author Nifury
 * @date 2017-11-1
 */
@Data
@TableName("wx_msg_reply_rule")
public class MsgReplyRule implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long ruleId;
    private String appid;
    private boolean sync = true;
    @NotEmpty(message = "规则名称不得为空")
    private String ruleName;
    @NotEmpty(message = "匹配关键词不得为空")
    private String matchValue;
    private boolean exactMatch;
    private String replyType;
    @NotEmpty(message = "回复内容不得为空")
    private String replyContent;
    @TableField(value = "`status`")
    private boolean status;
    @TableField(value = "`desc`")
    private String desc;
    @TableField(jdbcType = JdbcType.TIME)
    private Time effectTimeStart = Time.valueOf("00:00:00");
    @TableField(jdbcType = JdbcType.TIME)
    private Time effectTimeEnd = Time.valueOf("23:59:59");
    private int priority;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }

}
