package com.ly.bootadmin.sys.service;

import com.ly.bootadmin.sys.bean.SysRole;
import com.ly.bootadmin.sys.bean.SysUser;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author linyun
 * @date 2018/11/11 10:12
 */
public interface ISysUserService {
    Object datas(HttpServletRequest request);

    Object add(HttpServletRequest request);

    Object signin(HttpServletRequest request);

    void detial(HttpServletRequest request, Model model);

    Object registin(HttpServletRequest request);

    Object update(HttpServletRequest request);


    /**
     * 查询用户的生效的角色
     * @param username
     * @return
     */
    Set<SysRole> findUserRoles(String username);

    /**
     * 查询角色中生效的权限, 返回uri集合
     * @param id
     * @return
     */
    Set<String> findPrivilegesByRole(String id);

    /**
     * 用过登录名称查询得到用户信息
     * @param userName
     * @return
     */
    SysUser findOneByName(String userName);

    /**
     * 前往编辑页面
     * @param request
     * @param model
     */
    void gotoEdit(HttpServletRequest request,Model model);
}
