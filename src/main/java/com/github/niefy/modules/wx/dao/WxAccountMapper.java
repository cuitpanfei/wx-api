package com.github.niefy.modules.wx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.niefy.modules.wx.entity.WxAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公众号账号
 * 
 * @author niefy
 * @date 2020-06-17 13:56:51
 */
@Mapper
public interface WxAccountMapper extends BaseMapper<WxAccount> {
	
}
