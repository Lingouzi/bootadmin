package com.ly.bootadmin.security;

import com.ly.bootadmin.sys.bean.SysRole;
import com.ly.bootadmin.sys.bean.SysUser;
import com.ly.bootadmin.sys.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * @author linyun
 * @date 2018/11/20 21:11
 */
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private ISysUserService userService;

    /**
     * 进行权限验证
     * doGetAuthorizationInfo执行时机有三个，如下：
     * 1、subject.hasRole(“admin”) 或 subject.isPermitted(“admin”)：自己去调用这个是否有什么角色或者是否有什么权限的时候；
     * 2、@RequiresRoles("admin") ：在方法上加注解的时候；
     * 3、[@shiro.hasPermission name = "admin"][/@shiro.hasPermission]：在页面上加shiro标签的时候，即进这个页面的时候扫描到有这个标签的时候。
     * 权限信息.(授权):
     * 1、如果用户正常退出，缓存自动清空；
     * 2、如果用户非正常退出，缓存自动清空；
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
     * （需要手动编程进行实现；放在service进行调用）
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例，
     * 调用clearCached方法；
     * :Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
     * <p>
     * 如果没有缓存, 那么可能每次都会调用此方法:https://blog.csdn.net/qq_20954959/article/details/55260255
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("----权限验证--z");
        SimpleAuthorizationInfo authorizationInfo = null;
        try {
            Object principal = principals.getPrimaryPrincipal();
            authorizationInfo = new SimpleAuthorizationInfo();
            if (principal instanceof SysUser) {
                SysUser userLogin = (SysUser) principal;
                // 依据username从数据库中找到权限。
                Set<String> ps = new HashSet<>();
                SysUser user = userService.findOneByName(userLogin.getName());
                if (user == null) {
                    return authorizationInfo;
                }
                if (user.getState() == 0) {
                    return authorizationInfo;
                }
                if (user.getRoles() == null) {
                    return authorizationInfo;
                }

                for (String id : user.getRoles()) {
                    Set<SysRole> roles = userService.findUserRoles(userLogin.getName());
                    for (SysRole sysRole : roles) {
                        authorizationInfo.addRole(sysRole.getName());
                        if (sysRole.getPrivileges() != null && sysRole.getPrivileges().size() > 0) {
                            Set<String> privileges = userService.findPrivilegesByRole(id);
                            ps.addAll(privileges);
                        }
                    }
                }
                authorizationInfo.setStringPermissions(ps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorizationInfo;
    }

    /**
     * 验证用户身份
     * 1.调用currUser.login(token)方法时会调用doGetAuthenticationInfo方法
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("----登录验证--c");
        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        //获取用户的输入的账号.
        String username = (String) authenticationToken.getPrincipal();
        //通过username从数据库中查找 User对象，如果找到，没找到.
        SysUser user = userService.findOneByName(username);
        if (user != null) {
            if (user.getState() == 0) {
                throw new DisabledAccountException();
            }
            System.out.println("-----> 认证通过.");
            //使用账号作为盐值
            ByteSource slat = ByteSource.Util.bytes(username);
            return new SimpleAuthenticationInfo(
                    //用户名
                    user,
                    //密码
                    user.getPass(),
                    slat,
                    //realm name
                    getName()
            );
        }
        return null;
    }

    @Override
    protected void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        Cache c = getAuthenticationCache();
        log.info("清除【认证】缓存之前");
        for (Object o : c.keys()) {
            log.info(o + " , " + c.get(o));
        }
        super.clearCachedAuthenticationInfo(principals);
        log.info("调用父类清除【认证】缓存之后");
        for (Object o : c.keys()) {
            log.info(o + " , " + c.get(o));
        }

        // 添加下面的代码清空【认证】的缓存
        SysUser user = (SysUser) principals.getPrimaryPrincipal();
        SimplePrincipalCollection spc = new SimplePrincipalCollection(user.getName(), getName());
        super.clearCachedAuthenticationInfo(spc);
        log.info("添加了代码清除【认证】缓存之后");
        int cacheSize = c.keys().size();
        log.info("【认证】缓存的大小:" + c.keys().size());
        if (cacheSize == 0) {
            log.info("说明【认证】缓存被清空了。");
        }
    }

    /**
     * 会从redis缓存中找到 当前登录用户的权限缓存,清除掉. 但是用户的登录信息是没有清除的,所以用户再下次点击某个权限按钮的时候,
     * 会再次查询到用户最新的权限, 存储到redis中. 实现了,不需要用户退出系统,也不需要重启服务器的权限刷新
     * @param principals
     */
    @Override
    protected void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        Cache c = getAuthorizationCache();
        for (Object o : c.keys()) {
            log.info(o + " , " + c.get(o));
        }
        super.clearCachedAuthorizationInfo(principals);
        log.info("清除【授权】缓存之后");
        int cacheSize = c.keys().size();
        log.info("【授权】缓存的大小:" + cacheSize);

        for (Object o : c.keys()) {
            log.info(o + " , " + c.get(o));
        }
        if (cacheSize == 0) {
            log.info("说明【授权】缓存被清空了。");
        }
    }
}
