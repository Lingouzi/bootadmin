package com.ly.bootadmin.sys.service;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * @author linyun
 * @date 2018/11/20 12:01
 */
public interface ISysPrivilegeService {

    Object datas(HttpServletRequest request);

    Object add(HttpServletRequest request);

    Object refreshAll(HttpServletRequest request);

    /**
     *  重载所有权限,就是将系统后继添加的功能,加入到权限管理中, 之后还要刷新shiro权限才能起效
     *  比如后继系统添加了一个 /sysuser/deladd 的功能, 为了避免每次都手动添加权限, 直接做一个按钮, 主动刷新所有的权限
     * @param request
     * @return
     */
    Object reloadAllPrivileges(HttpServletRequest request);

    void gotoRelationShip(Model model);

    /**
     * 编辑权限之间的父子关系
     * @param request
     * @return
     */
    Object relationship(HttpServletRequest request);

    Object update(HttpServletRequest request);
}
