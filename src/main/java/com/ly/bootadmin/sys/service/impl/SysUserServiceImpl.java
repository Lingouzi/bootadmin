package com.ly.bootadmin.sys.service.impl;

import com.ly.bootadmin.sys.bean.SysPrivilege;
import com.ly.bootadmin.sys.bean.SysRole;
import com.ly.bootadmin.sys.bean.SysUser;
import com.ly.bootadmin.sys.repo.SysPrivilegeRepository;
import com.ly.bootadmin.sys.repo.SysRoleRepository;
import com.ly.bootadmin.sys.repo.SysUserRepository;
import com.ly.bootadmin.sys.service.ISysUserService;
import com.ly.bootadmin.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author linyun
 * @date 2018/11/11 10:12
 */
@Slf4j
@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private MongoPageHelper mongoPageHelper;

    @Autowired
    private MongoOperations operations;

    @Autowired
    private SysRoleRepository roleRepository;

    @Autowired
    private SysPrivilegeRepository privilegeRepository;

    @Override
    public Object datas(HttpServletRequest request) {
        String key = request.getParameter("key");
        String stime = request.getParameter("stime");
        String etime = request.getParameter("etime");
        String state = request.getParameter("state");
        int pageSize = Integer.parseInt(request.getParameter("pageSize") == null ? "10" : request.getParameter("pageSize"));
        int pageNum = Integer.parseInt(request.getParameter("pageNum") == null ? "0" : request.getParameter("pageNum"));
        try {
            Criteria c = new Criteria();
            if (StringUtils.isNotBlank(key)) {
                //忽略大小写的模糊查询
                Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
                c.orOperator(Criteria.where("name").regex(pattern), Criteria.where("nick").regex(pattern));
            }
            if (StringUtils.isNotBlank(stime) && StringUtils.isNotBlank(etime)) {
                // 时间段
                long s = DateUtils.parseDate(stime, SysContent.YYYY_MM_DD).getTime();
                long e = DateUtils.parseDate(etime, SysContent.YYYY_MM_DD).getTime();
                c.and("date").lt(e).gt(s);
            }
            if (!StringUtils.isEmpty(state)) {
                c.and("state").is(Integer.parseInt(state));
            }
            return mongoPageHelper.pageQuery(Query.query(c), SysUser.class, pageSize, pageNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResult<>();
    }

    @Override
    public Object add(HttpServletRequest request) {
        String name = request.getParameter("name");
        // 对密码进行md5加密, 并且进行加盐,如果更加进一步的安全,可以在注册页面,使用js对密码加密,参考:security.js
        String pass = request.getParameter("pass");
        String nick = request.getParameter("nick");
        String avatarUrl = request.getParameter("avatarUrl");
        String state = request.getParameter("state");

        SysUser user = userRepository.findByName(name);
        if (user == null) {
            // md5加密, 使用name当成盐, 加密31次.
            Object result = new SimpleHash("MD5", pass, name, 31);
            user = SysUser.builder()
                    .id(IDMarker.getInstance().nextId() + "")
                    .date(System.currentTimeMillis())
                    .avatarUrl(avatarUrl)
                    .name(name)
                    .pass(result.toString())
                    .nick(nick)
                    .roles(new HashSet<>())
                    .state(Integer.parseInt(state))
                    .build();
            userRepository.save(user);
        } else {
            return JsonResp.fail("用户已存在");
        }
        return JsonResp.success(user);
    }

    @Override
    public Object signin(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberme = request.getParameter("rememberme");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return JsonResp.fail("账号密码不能为空");
        }

        try {
            SysUser user = userRepository.findByName(username);
            if (user == null) {
                return JsonResp.fail("账号或者密码不正确");
            }
            // 结合shiro做认证
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            try {
                log.info("rememberMe:" + rememberme);
                if (StringUtils.isNotBlank(rememberme) && "1".equals(rememberme)) {
                    token.setRememberMe(true);
                }
                subject.login(token);
            } catch (LockedAccountException e) {
                return JsonResp.fail("用户被锁定 30 分钟");
            } catch (DisabledAccountException e) {
                return JsonResp.fail("账号已注销");
            } catch (AuthenticationException e) {
                token.clear();
                return JsonResp.fail("账号密码错误,登录失败.");
            }
            return JsonResp.success();
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResp.fail("登录信息异常");
        }
    }

    @Override
    public void detial(HttpServletRequest request, Model model) {
        String id = request.getParameter("id");
        Optional<SysUser> userOptional = userRepository.findById(id);
        SysUser user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        model.addAttribute("user", user);
    }

    @Override
    public Object registin(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String code = request.getParameter("code");

        if (!StringUtils.isEmpty(code) && !"bootAdmin".equalsIgnoreCase(code)) {
            return JsonResp.fail("推荐码错误");
        }

        // 是否重复注册
        long num = userRepository.countByName(username);
        if (num > 0) {
            return JsonResp.fail("登录账号被占用");
        }
        Object result = new SimpleHash("MD5", password, username, 31);
        SysUser user = SysUser.builder()
                .id(IDMarker.getInstance().nextId() + "")
                .date(System.currentTimeMillis())
                .name(username)
                .pass(result.toString())
                .nick("user" + IDMarker.getInstance().nextId())
                .avatarUrl("")
                .state(1)
                .build();
        userRepository.save(user);
        return JsonResp.success();
    }

    @Override
    public Object update(HttpServletRequest request) {
        return DataUtils.updateById(request, SysUser.class);
    }

    @Override
    public Set<SysRole> findUserRoles(String username) {
        SysUser user = userRepository.findByName(username);
        if (user != null) {
            Set<SysRole> ro = new HashSet<>();
            Set<String> roles = user.getRoles();
            for (String id : roles) {
                SysRole role = roleRepository.findById(id).orElse(null);
                if (role != null && role.getState() == 1) {
                    ro.add(role);
                }
            }
            return ro;
        }
        return null;
    }

    /**
     * 获取角色的权限, 生效的
     * @param id
     * @return
     */
    @Override
    public Set<String> findPrivilegesByRole(String id) {
        SysRole role = roleRepository.findById(id).orElse(null);
        if (role != null) {
            // 得到权限id
            Set<String> privileges = new HashSet<>();
            for (String pid : role.getPrivileges()) {
                SysPrivilege privilege = privilegeRepository.findById(pid).orElse(null);
                if (privilege != null && privilege.getState() == 1) {
                    privileges.add(privilege.getUri());
                }
            }
            return privileges;
        }
        return null;
    }

    @Override
    public SysUser findOneByName(String userName) {
        return userRepository.findByName(userName);
    }

    @Override
    public void gotoEdit(HttpServletRequest request, Model model) {
        String id = request.getParameter("id");
        SysUser user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
    }

}
