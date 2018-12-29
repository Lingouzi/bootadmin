package com.ly.bootadmin.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: linyun 664162337@qq.com
 * @Date: 2018/11/8 008 17:41
 */
@Data
@Component
@ConfigurationProperties(prefix = "settings")
public class BaseSetting {

    /**
     * 当前激活的系统版本
     * test还是pro或者dev
     */
    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 线程池初始大小.
     */
    private Integer coreThreadPoolNum;

    /**
     * 上传文件保存地址
     */
    private String uploadFileSavePath;
}
