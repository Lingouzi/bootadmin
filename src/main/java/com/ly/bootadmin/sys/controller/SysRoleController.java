package com.ly.bootadmin.sys.controller;

import com.ly.bootadmin.sys.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author linyun
 * @date 2018/11/20 17:38
 */
@Controller
@RequestMapping("/sysrole")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;


    /**
     * 加载数据页
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String list(HttpServletRequest request, Model model){
        return "sys/role/list";
    }

    /**
     * 前往添加页
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/gotoAdd")
    public String gotoAdd(HttpServletRequest request, Model model){
        roleService.gotoAdd(model);
        return "sys/role/add";
    }

    /**
     * 前往编辑页面
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/gotoEdit")
    public String gotoEdit(HttpServletRequest request, Model model){
        roleService.gotoEdit(request,model);
        return "sys/role/edit";
    }

    /**
     * 查看 角色 页面
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/gotoShow")
    public String gotoShow(HttpServletRequest request, Model model){
        roleService.gotoShow(request,model);
        return "sys/role/show";
    }

    /**
     * 加载分页数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/datas")
    public Object datas(HttpServletRequest request){
        return roleService.datas(request);
    }

    /**
     * 添加
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/add")
    public Object add(HttpServletRequest request){
        return roleService.add(request);
    }

    /**
     * 修改
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/edit")
    public Object edit(HttpServletRequest request){
        return roleService.edit(request);
    }

    /**
     * 单项更新
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/update")
    public Object update(HttpServletRequest request){
        return roleService.update(request);
    }

    /**
     * 删除角色
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/delete")
    public Object delete(HttpServletRequest request){
        return roleService.delete(request);
    }

}
