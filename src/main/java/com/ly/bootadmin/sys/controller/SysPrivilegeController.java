package com.ly.bootadmin.sys.controller;

import com.ly.bootadmin.sys.service.ISysPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author linyun
 * @date 2018/11/20 12:13
 */
@Controller
@RequestMapping("/privilege")
public class SysPrivilegeController {

    @Autowired
    private ISysPrivilegeService privilegeService;

    /**
     * 加载数据页
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String list(HttpServletRequest request, Model model) {
        return "sys/privilege/list";
    }

    /**
     * 前往添加页
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/gotoAdd")
    public String gotoAdd(HttpServletRequest request, Model model) {
        return "sys/privilege/add";
    }

    /**
     * 前往编辑页面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/gotoEdit")
    public String gotoEdit(HttpServletRequest request, Model model) {
        return "sys/privilege/edit";
    }

    @RequestMapping("/gotoRelationShip")
    public String gotoRelationShip(HttpServletRequest request, Model model) {
        privilegeService.gotoRelationShip(model);
        return "sys/privilege/relationship";
    }

    /**
     * 加载分页数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/datas")
    public Object datas(HttpServletRequest request) {
        return privilegeService.datas(request);
    }

    /**
     * 添加
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/add")
    public Object add(HttpServletRequest request) {
        return privilegeService.add(request);
    }

    /**
     * 修改
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/edit")
    public Object edit(HttpServletRequest request) {
        return null;
    }

    /**
     * 单项更新
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/update")
    public Object update(HttpServletRequest request) {
        return privilegeService.update(request);
    }

    /**
     * 刷新shiro的权限
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/refreshAll")
    public Object refreshAll(HttpServletRequest request) {
        return privilegeService.refreshAll(request);
    }

    @ResponseBody
    @RequestMapping("/reloadAllPrivileges")
    public Object reloadAllPrivileges(HttpServletRequest request) {
        return privilegeService.reloadAllPrivileges(request);
    }

    @ResponseBody
    @RequestMapping("/relationship")
    public Object relationship(HttpServletRequest request) {
        return privilegeService.relationship(request);
    }

}
