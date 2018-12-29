package com.ly.bootadmin.web.listener;

import com.ly.bootadmin.sys.bean.SysRole;
import com.ly.bootadmin.sys.bean.SysUser;
import com.ly.bootadmin.sys.repo.SysPrivilegeRepository;
import com.ly.bootadmin.sys.repo.SysRoleRepository;
import com.ly.bootadmin.sys.repo.SysUserRepository;
import com.ly.bootadmin.sys.utils.PrivilegeUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: linyun 664162337@qq.com
 * @Date: 2018/11/10 010 14:23
 */
public class InitListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private SysRoleRepository roleRepository;

    @Autowired
    private SysPrivilegeRepository privilegeRepository;

    @Autowired
    private SysUserRepository userRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println(">>>>>>>>>>> 调用 ApplicationStartedEvent <<<<<<<<<<<< ");
        SysRole role = roleRepository.findById("0").orElse(null);
        if (role == null) {
            RequestMappingHandlerMapping bean = event.getApplicationContext().getBean(RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
            Set<String> result = new HashSet<>();
            for (RequestMappingInfo rmi : handlerMethods.keySet()) {
                PatternsRequestCondition pc = rmi.getPatternsCondition();
                Set<String> pSet = pc.getPatterns();
                result.addAll(pSet);
            }
            if (result.size() > 0) {
                Set<String> ps = new HashSet<>();
                PrivilegeUtils.addPrivilegesToDataBase(result, ps);
                // 创建超级管理员角色
                role = SysRole.builder()
                        .id("0")
                        .date(System.currentTimeMillis())
                        .name("ROLE_SUPERADMIN")
                        .desc("超级管理员")
                        .state(1)
                        .privileges(ps)
                        .build();
                roleRepository.save(role);

                //创建超级管理员账号
                //
                Set<String> roles = new HashSet<>();
                roles.add("0");
                Object r = new SimpleHash("MD5", "admin", "admin", 31);
                SysUser user = SysUser.builder()
                        .id("1")
                        .date(System.currentTimeMillis())
                        .nick("超级管理员")
                        .name("admin")
                        .pass(r.toString())
                        .avatarUrl("")
                        .state(1)
                        .roles(roles)
                        .build();
                userRepository.save(user);
            }
        }
    }
}
