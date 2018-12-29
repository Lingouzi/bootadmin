package com.ly.bootadmin.security.config;

import com.ly.bootadmin.redis.RedisProperties;
import com.ly.bootadmin.redis.ShiroPassWordRedisManager;
import com.ly.bootadmin.security.RetryLimitHashedCredentialsMatcher;
import com.ly.bootadmin.security.ShiroRealm;
import com.ly.bootadmin.security.filter.KickoutSessionControlFilter;
import com.ly.bootadmin.sys.bean.SysPrivilege;
import com.ly.bootadmin.sys.repo.SysPrivilegeRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linyun
 * @date 2018/11/20 21:10
 */
@Configuration
public class ShiroConfig {

    private final RedisProperties properties;

    private final SysPrivilegeRepository privilegeRepository;

    private static final int DEFAULT_EXPIRE = 1800;

    private static final int DEFAULT_MAX_AGE = DEFAULT_EXPIRE * 2 * 30 * 24;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ShiroConfig(RedisProperties properties, SysPrivilegeRepository privilegeRepository, RedisTemplate<String, Object> redisTemplate) {
        this.properties = properties;
        this.privilegeRepository = privilegeRepository;
        this.redisTemplate = redisTemplate;
    }


    @Bean
    public ShiroFilterFactoryBean shirFilter() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        // 自定义过滤器
        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
        filtersMap.put("kickout", kickoutSessionControlFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);
        Map<String, String> filter = new LinkedHashMap<>(16);
        List<SysPrivilege> list = privilegeRepository.findAll();
        for (SysPrivilege privilege : list) {
            if (StringUtils.isNotBlank(privilege.getUri())) {
                if ("/".equals(privilege.getUri())
                        || "/index".equals(privilege.getUri())
                        || "/error".equals(privilege.getUri())
                        || "/login".equals(privilege.getUri())
                ) {
                    continue;
                }
                // 所有权限都加上踢人的过滤器, 异地登录之后,踢出另外的用户
                filter.put(privilege.getUri(), "perms[\"" + privilege.getUri() + "\"],kickout");
            }
        }
        filter.put("/static/**", "anon");
        filter.put("/signin", "anon");
        filter.put("/registin", "anon");
        filter.put("/regist", "anon");
        filter.put("/favicon.ico", "anon");
        filter.put("/logout", "logout");

        filter.put("/**", "kickout,authc");

        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filter);
        return shiroFilterFactoryBean;
    }

    /**
     * 并发登录控制
     *
     * @return
     */
    public KickoutSessionControlFilter kickoutSessionControlFilter() {
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        //用于根据会话ID，获取会话进行踢出操作的；
        kickoutSessionControlFilter.setSessionManager(sessionManager());
        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        kickoutSessionControlFilter.setCacheManager(cacheManager());
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
        kickoutSessionControlFilter.setKickoutAfter(false);
        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
        kickoutSessionControlFilter.setMaxSession(1);
        //被踢出后重定向到的地址；
        kickoutSessionControlFilter.setKickoutUrl("/login?kickout=1");
        return kickoutSessionControlFilter;
    }

    /**
     * cacheManager 缓存 redis实现
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        //redis中针对不同用户缓存
//        redisCacheManager.setPrincipalIdFieldName("username");
        //用户权限信息缓存时间, 单位s
        redisCacheManager.setExpire(30 * 60);
        return redisCacheManager;
    }

    /**
     * 配置shiro redisManage
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    public RedisManager redisManager() {
        // 这里使用的redisManager 不是自定义的,而是 插件中的, 注意参数的引用.
        RedisManager redisManager = new RedisManager();
        redisManager.setPassword(properties.getPassword());
        redisManager.setHost(properties.getHost() + ":" + properties.getPort());
        redisManager.setDatabase(properties.getDatabase());
        redisManager.setTimeout(Integer.parseInt(properties.getShutdown().contains("ms") ? properties.getShutdown().replaceAll("ms", "") : properties.getShutdown()));
        return redisManager;
    }

    /**
     * Session Manager
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        // 修改为自定义的sessionmamager,
        ShiroSessionManager sessionManager = new ShiroSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        //全局会话超时时间（单位毫秒），默认30分钟
        sessionManager.setGlobalSessionTimeout(DEFAULT_EXPIRE * 1000);
        //是否开启删除无效的session对象  默认为true
        sessionManager.setDeleteInvalidSessions(true);
        //是否开启定时调度器进行检测过期session 默认为true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        sessionManager.setSessionValidationInterval(DEFAULT_EXPIRE * 2 * 1000);
        //取消url 后面的 JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
     * MemorySessionDAO 直接在内存中进行会话维护
     * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
     * <p>
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        //session在redis中的保存时间,最好大于session会话超时时间,单位s
        redisSessionDAO.setExpire(DEFAULT_EXPIRE + 1);
        return redisSessionDAO;
    }

    /**
     * 配置shiro的关键配置之一,需要用户手动配置.
     *
     * @return
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.
        securityManager.setRealm(shiroRealm());
        // https://www.cnblogs.com/caichaoqi/p/8900677.html
        // 自定义缓存实现 使用redis
        securityManager.setCacheManager(cacheManager());
        // 自定义session管理 使用redis
        securityManager.setSessionManager(sessionManager());
        // https://www.cnblogs.com/aqsunkai/p/6690570.html
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    /**
     * rememberme的cookie自定义设置,默认是1年
     *
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("BOOTADMINREMEMBERME");
        //setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数。它有以下特点：
        //setcookie()的第七个参数
        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        // cookie 保存 30天
        simpleCookie.setMaxAge(DEFAULT_MAX_AGE);
        return simpleCookie;
    }

    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.getDecoder().decode("NgDSkte66tvjkxpxRs3kTKybBTyEdUbK"));
        return cookieRememberMeManager;
    }


    /**
     * 身份认证realm,账号密码校验；权限等;
     *
     * @return
     */
    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm myShiroRealm = new ShiroRealm();
        myShiroRealm.setCredentialsMatcher(retryLimitHashedCredentialsMatcher());
        return myShiroRealm;
    }

    /**
     * 使用自定义的方法,实现登录次数限制, 错误次数太多就限制登录.
     *
     * @return
     */
    @Bean("credentialsMatcher")
    public RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher() {
        RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher();
        retryLimitHashedCredentialsMatcher.setRedisManager(new ShiroPassWordRedisManager(redisTemplate));
        //加密算法的名称
        retryLimitHashedCredentialsMatcher.setHashAlgorithmName("MD5");
        //配置加密的次数
        retryLimitHashedCredentialsMatcher.setHashIterations(31);
        //是否存储为16进制
        //retryLimitHashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return retryLimitHashedCredentialsMatcher;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     * 开启 权限注解
     * Controller才能使用@RequiresPermissions
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
