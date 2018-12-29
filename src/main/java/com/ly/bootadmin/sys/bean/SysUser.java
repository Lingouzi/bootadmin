package com.ly.bootadmin.sys.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * 系统用户
 * @author linyun
 * @date 2018/10/27 17:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser implements Serializable {

    /**
     * 因为id超过16为，在页面显示会出现精度丢失的问题，这里转为json的时候，转为string类型
     * @JsonSerialize(using= ToStringSerializer.class)
     * 原来id是long型的,需要加注解转为string
     */
    private String id;
    private Long date;

    /**
     * 登录账号,密码
     * 使用jsonproperty注解,可以指定在bean转为json的时候,将name属性名称设定为loginName
     */
    private String name;
    /**
     * 在生成json的时候, 去除此属性
     */
    @JsonIgnore
    private String pass;

    /**
     * 昵称
     */
    private String nick;
    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 用户的角色.
     */
    private Set<String> roles;

    /**
     * 0:被注销
     * 1:生效的账号
     */
    private Integer state;

}
