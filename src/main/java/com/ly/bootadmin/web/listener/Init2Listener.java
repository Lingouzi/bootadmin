package com.ly.bootadmin.web.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;

/**
 * @Author: linyun 664162337@qq.com
 * @Date: 2018/11/10 010 14:23
 */
public class Init2Listener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println(">>>>>>>>>>> 调用 ApplicationReadyEvent <<<<<<<<<<<< ");
    }
}
