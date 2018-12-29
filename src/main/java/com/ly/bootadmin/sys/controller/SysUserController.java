package com.ly.bootadmin.sys.controller;

import com.ly.bootadmin.sys.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author linyun
 * @date 2018/11/11 00:17
 */
@Controller
@RequestMapping("/sysuser")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 加载数据页
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String list(HttpServletRequest request, Model model){
        return "sys/user/list";
    }

    /**
     * 前往添加页
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/gotoAdd")
    public String gotoAdd(HttpServletRequest request, Model model){
        return "sys/user/add";
    }

    /**
     * 前往编辑页面
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/gotoEdit")
    public String gotoEdit(HttpServletRequest request, Model model){
        userService.gotoEdit(request,model);
        return "sys/user/edit";
    }

    /**
     * 详情界面
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/detial")
    public String detial(HttpServletRequest request, Model model) {
        userService.detial(request,model);
        return "sys/user/detial";
    }
    /**
     * 加载分页数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/datas")
    public Object datas(HttpServletRequest request){
        return userService.datas(request);
    }

    /**
     * 添加
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/add")
    public Object add(HttpServletRequest request){
        return userService.add(request);
    }

    /**
     * 修改
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/edit")
    public Object edit(HttpServletRequest request){
        return null;
    }

    /**
     * 单项更新
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/update")
    public Object update(HttpServletRequest request){
        return userService.update(request);
    }

}
