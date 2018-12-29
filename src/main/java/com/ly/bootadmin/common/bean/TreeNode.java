package com.ly.bootadmin.common.bean;

import lombok.Builder;
import lombok.Data;

/**
 * ztree 的数据结构, 简化版, ztree文档:http://www.treejs.cn/v3/faq.php#_206
 * @author linyun
 * @date 2018/11/24 11:55
 */
@Data
@Builder
public class TreeNode {

    /**
     * 节点id
     */
    private String id;
    /**
     * 父节点id
     */
    private String pId;
    /**
     *  节点显示名称
     */
    private String name;

    /**
     * 记录 treeNode 节点的 展开 / 折叠 状态
     */
    private boolean open;
    /**
     * checkBox / radio 的 勾选状态
     */
    private boolean checked;
    /**
     * 是否能够拖动
     */
    private boolean drag;

    /**
     * 禁止子节点移走
     */
    private boolean childOuter;
    /**
     * 不想成为父节点
     */
    private boolean dropInner;

}
