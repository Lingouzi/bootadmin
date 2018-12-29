package com.ly.bootadmin.sys.service;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * @author linyun
 * @date 2018/11/20 17:39
 */
public interface ISysRoleService {
    Object datas(HttpServletRequest request);

    void gotoAdd(Model model);

    Object update(HttpServletRequest request);

    Object add(HttpServletRequest request);

    Object edit(HttpServletRequest request);

    void gotoEdit(HttpServletRequest request, Model model);

    void gotoShow(HttpServletRequest request, Model model);

    Object delete(HttpServletRequest request);
}
