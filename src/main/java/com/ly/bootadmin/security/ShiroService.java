package com.ly.bootadmin.security;

import com.ly.bootadmin.sys.bean.SysPrivilege;
import com.ly.bootadmin.sys.repo.SysPrivilegeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linyun
 * @date 2018/11/20 22:00
 */
@Slf4j
@Service
public class ShiroService {

    private final ShiroFilterFactoryBean shiroFilterFactoryBean;

    private final SysPrivilegeRepository privilegeRepository;

    @Autowired
    public ShiroService(ShiroFilterFactoryBean shiroFilterFactoryBean, SysPrivilegeRepository privilegeRepository) {
        this.shiroFilterFactoryBean = shiroFilterFactoryBean;
        this.privilegeRepository = privilegeRepository;
    }

    /**
     * 初始化权限
     */
    public Map<String, String> loadFilterChainDefinitions() {
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
        return filter;
    }

    /**
     * 重新加载权限
     */
    public void updatePrivileges() {
        synchronized (shiroFilterFactoryBean) {
            AbstractShiroFilter shiroFilter;
            try {
                shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean
                        .getObject();
            } catch (Exception e) {
                throw new RuntimeException(
                        "get ShiroFilter from shiroFilterFactoryBean error!");
            }

            PathMatchingFilterChainResolver filterChainResolver = null;
            if (shiroFilter != null) {
                filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                        .getFilterChainResolver();
            }
            DefaultFilterChainManager manager;
            if (filterChainResolver != null) {
                manager = (DefaultFilterChainManager) filterChainResolver
                        .getFilterChainManager();

                // 清空老的权限控制
                manager.getFilterChains().clear();

                shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
                shiroFilterFactoryBean
                        .setFilterChainDefinitionMap(loadFilterChainDefinitions());
                // 重新构建生成
                Map<String, String> chains = shiroFilterFactoryBean
                        .getFilterChainDefinitionMap();
                for (Map.Entry<String, String> entry : chains.entrySet()) {
                    String url = entry.getKey();
                    String chainDefinition = entry.getValue().trim()
                            .replace(" ", "");
                    manager.createChain(url, chainDefinition);
                }

                log.info("shiro 权限更新权限成功！！");

                // 下面更新用户的权限缓存
                RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
                ShiroRealm realm = (ShiroRealm) rsm.getRealms().iterator().next();
                realm.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());

                log.info("用户的权限刷新成功.");
            }
        }
    }
}
