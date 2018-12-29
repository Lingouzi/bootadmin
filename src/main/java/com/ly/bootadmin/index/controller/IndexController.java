package com.ly.bootadmin.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: linyun 664162337@qq.com
 * @Date: 2018/11/8 008 17:53
 */
@Controller
public class IndexController {

    @RequestMapping({"/", "/index"})
    public String index() {
        return "index";
    }
}
