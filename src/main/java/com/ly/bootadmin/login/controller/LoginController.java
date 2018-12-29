package com.ly.bootadmin.login.controller;

import com.ly.bootadmin.sys.service.ISysUserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: linyun 664162337@qq.com
 * @Date: 2018/11/8 008 18:09
 */
@Controller
public class LoginController {

    @Autowired
    private ISysUserService userService;

    /**
     * 到登录界面
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model){
        return "login";
    }


    @RequestMapping("/regist")
    public String regist(HttpServletRequest request, Model model){
        return "regist";
    }


    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, Model model){
        request.getSession().removeAttribute("bootAdminUser");
        SecurityUtils.getSubject().logout();
        return "redirect:/login";
    }

    /**
     * 提交登陆信息
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/signin")
    public Object signin(HttpServletRequest request) {
        return userService.signin(request);
    }

    @ResponseBody
    @PostMapping("/registin")
    public Object registin(HttpServletRequest request) {
        return userService.registin(request);
    }

}
