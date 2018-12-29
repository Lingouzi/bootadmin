package com.ly.bootadmin.sys.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * 角色表
 *
 * @author linyun
 * @date 2018/11/5 15:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysRole implements Serializable {
    private String id;
    private Long date;

    /**
     * 角色名称
     */
    private String name;
    /**
     * 说明
     */
    private String desc;

    /**
     * 角色包含的权限
     */
    private Set<String> privileges;

    /**
     * 0:实效
     * 1:生效
     */
    private Integer state;
}
