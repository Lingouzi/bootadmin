package com.ly.bootadmin.sys.utils;

import com.ly.bootadmin.common.bean.TreeNode;
import com.ly.bootadmin.sys.bean.SysPrivilege;
import com.ly.bootadmin.sys.repo.SysPrivilegeRepository;
import com.ly.bootadmin.utils.IDMarker;
import com.ly.bootadmin.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author linyun
 * @date 2018/11/24 15:40
 */
@Component
public class PrivilegeUtils {

    private static SysPrivilegeRepository privilegeRepository;

    @Autowired
    public PrivilegeUtils(SysPrivilegeRepository privilegeRepository) {
        PrivilegeUtils.privilegeRepository = privilegeRepository;
    }

    /**
     * 加载所有生效的权限, 生成 tree 数据结构
     *
     * @param model
     */
    public static void privilegesToTreeNode(Model model) {
        List<TreeNode> treeNodes = new ArrayList<>();
        List<SysPrivilege> privileges = privilegeRepository.findByState(1);
        for (SysPrivilege privilege : privileges) {
            TreeNode node = TreeNode.builder()
                    .id(privilege.getId() + "")
                    .checked(false)
                    .name(privilege.getName() + "[" + privilege.getUri() + "]")
                    .pId(privilege.getPid() != null ? privilege.getPid() + "" : "")
                    .build();
            treeNodes.add(node);
        }
        try {
            model.addAttribute("treeNodes", JsonUtils.obj2jsonIgnoreNull(treeNodes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 找到系统中所有的uri, 变成 权限.
     *
     * @param result
     * @param uris
     */
    public static void addPrivilegesToDataBase(Set<String> result, Set<String> uris) {
        for (String uri : result) {
            if ("/".equals(uri)
                    || "/index".equals(uri)
                    || "/error".equals(uri)
                    || "/login".equals(uri)
                    || "/signin".equals(uri)
                    || "/registin".equals(uri)
                    || "/regist".equals(uri)
                    || "/favicon.ico".equals(uri)
                    || "/logout".equals(uri)
                    || uri.startsWith("/static")
            ) {
                continue;
            }
            // 系统中不存在的, 添加
            if (!privilegeRepository.existsByUri(uri)) {
                String id = IDMarker.getInstance().nextId() + "";
                SysPrivilege privilege = SysPrivilege.builder()
                        .id(id + "")
                        .date(System.currentTimeMillis())
                        .name("")
                        .desc("")
                        .uri(uri)
                        .pid(null)
                        .state(1)
                        .build();
                privilegeRepository.save(privilege);
                uris.add(id);
            }

        }
    }

    /**
     * 依据角色拥有的权限, 查询权限库, 得到勾选结构的 tree
     *
     * @param model
     * @param privileges
     */
    public static void rolePrivilegesToTreeNode(Model model, Set<String> privileges) {
        List<TreeNode> treeNodes = new ArrayList<>();
        List<SysPrivilege> all = privilegeRepository.findAll();
        for (SysPrivilege privilege : all) {
            boolean checked = false;
            if (privileges.contains(privilege.getId())) {
                checked = true;
            }
            TreeNode node = TreeNode.builder()
                    .id(privilege.getId() + "")
                    .checked(checked)
                    .name(privilege.getName() + "[" + privilege.getUri() + "]")
                    .pId(privilege.getPid() != null ? privilege.getPid() + "" : "")
                    .build();
            treeNodes.add(node);
        }
        try {
            model.addAttribute("treeNodes", JsonUtils.obj2jsonIgnoreNull(treeNodes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询角色已包含的角色
     * @param model
     * @param privileges
     */
    public static void rolePrivilegesToTreeNodeCheck(Model model, Set<String> privileges) {
        List<TreeNode> treeNodes = new ArrayList<>();
        for (String id : privileges) {
            SysPrivilege privilege = privilegeRepository.findById(id).orElse(null);
            if(privilege != null && privilege.getState() == 1){
                TreeNode node = TreeNode.builder()
                        .id(privilege.getId() + "")
                        .checked(true)
                        .name(privilege.getName() + "[" + privilege.getUri() + "]")
                        .pId(privilege.getPid() != null ? privilege.getPid() + "" : "")
                        .build();
                treeNodes.add(node);
            }
        }
        try {
            model.addAttribute("treeNodes", JsonUtils.obj2jsonIgnoreNull(treeNodes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
