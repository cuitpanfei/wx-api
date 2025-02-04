package com.github.niefy.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.IPUtils;
import com.github.niefy.common.utils.Result;
import com.github.niefy.modules.sys.dao.SysUserTokenDao;
import com.github.niefy.modules.sys.entity.SysUserTokenEntity;
import com.github.niefy.modules.sys.oauth2.TokenGenerator;
import com.github.niefy.modules.sys.service.SysUserTokenService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;


@Service("sysUserTokenService")
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {
    //12小时后过期
    private final static int EXPIRE = 3600 * 12;


    @Override
    public Result createToken(long userId) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

        //判断是否生成过token
        SysUserTokenEntity tokenEntity = this.getById(userId);
        if (tokenEntity == null) {
            tokenEntity = new SysUserTokenEntity();
            tokenEntity.setUserId(userId);
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);

            //保存token
            this.save(tokenEntity);
        } else {
            if (IPUtils.isNotLocalRequest() && tokenEntity.getExpireTime().getTime() == -1) {
                return Result.error("账号已过期,请联系管理员");
            }
            String oldToken = tokenEntity.getToken();
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);

            //更新token
            boolean flag = this.update(tokenEntity, new QueryWrapper<SysUserTokenEntity>().eq("user_id", userId).ne("expire_time", -1));
            if (!flag) {
                token = oldToken;
            }
        }

        return Objects.requireNonNull(Result.ok().put("token", token)).put("expire", EXPIRE);
    }

    @Override
    public void logout(long userId) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //修改token
        SysUserTokenEntity tokenEntity = new SysUserTokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(token);
        this.update(tokenEntity,
                new QueryWrapper<SysUserTokenEntity>().eq("user_id", userId).ne("expire_time", -1));
    }
}
