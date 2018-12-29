package com.ly.bootadmin.sys.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 配合shiro进行权限管理, 权限细化到每一个按钮.
 * @author linyun
 * @date 2018/11/5 16:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysPrivilege implements Serializable {

    private String id;
    private Long date;

    /**
     * 名称
     */
    private String name;
    /**
     * 具体的uri, 格式: /sysuser/add
     */
    private String uri;
    /**
     * 说明
     */
    private String desc;

    /**
     * 父级权限的id
     */
    private String pid;

    /**
     * 如果是父级,下面的一系列的都会是不加载的.
     * 0:注销
     * 1:启用
     */
    private Integer state;
}
