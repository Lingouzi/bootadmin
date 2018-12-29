package com.ly.bootadmin.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果
 * @author linyun
 * @date 2018/11/6 10:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 全部数据条数
     */
    private Long total = 0L;

    /**
     * 总页数
     */
    private Integer pages = 0;

    /**
     * 数据
     */
    private List<T> list = new ArrayList<>();
}
