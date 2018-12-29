package com.ly.bootadmin.security;

import com.ly.bootadmin.redis.ShiroPassWordRedisManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登陆次数限制
 *
 * @author linyun
 * @date 2018/11/23 10:56
 */
@Slf4j
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    private static final String DEFAULT_RETRYLIMIT_CACHE_KEY_PREFIX = "shiro:cache:retrylimit:";
    private ShiroPassWordRedisManager redisManager;

    public void setRedisManager(ShiroPassWordRedisManager redisManager) {
        this.redisManager = redisManager;
    }

    private String getRedisKickoutKey(String username) {
        return DEFAULT_RETRYLIMIT_CACHE_KEY_PREFIX + username;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //获取用户名
        String username = (String) token.getPrincipal();
        //获取用户登录次数
        // 从redis找到是否有记录次数
        AtomicInteger retryCount = (AtomicInteger) redisManager.get(getRedisKickoutKey(username));
        if (retryCount == null) {
            //如果用户没有登陆过,登陆次数加1 并放入缓存
            retryCount = new AtomicInteger(0);
        }
        // 如果次数为5 , 抛出锁定的提示
        if (retryCount.incrementAndGet() > 5) {
            //如果用户登陆失败次数大于5次 抛出锁定用户异常, 30分钟后解除锁定
            redisManager.set(getRedisKickoutKey(username), retryCount, 1800);
            //抛出用户锁定异常
            throw new LockedAccountException();
        }
        //判断用户账号和密码是否正确
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //如果正确,从缓存中将用户登录计数 清除
            unlockAccount(username);
        } else {
            // 登陆错误的记录, 30分钟后清除.
            redisManager.set(getRedisKickoutKey(username), retryCount, 1800);
        }
        return matches;
    }

    /**
     * 根据用户名 解锁用户
     *
     * @param username
     * @return
     */
    public void unlockAccount(String username) {
        redisManager.del(getRedisKickoutKey(username));
    }
}
