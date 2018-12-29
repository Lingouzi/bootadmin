package com.ly.bootadmin.sys.service.impl;

import com.ly.bootadmin.sys.bean.SysRole;
import com.ly.bootadmin.sys.repo.SysRoleRepository;
import com.ly.bootadmin.sys.service.ISysRoleService;
import com.ly.bootadmin.sys.utils.PrivilegeUtils;
import com.ly.bootadmin.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author linyun
 * @date 2018/11/20 17:39
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    @Autowired
    private MongoPageHelper mongoPageHelper;

    @Autowired
    private SysRoleRepository roleRepository;

    @Override
    public Object datas(HttpServletRequest request) {
        String key = request.getParameter("key");
        String start = request.getParameter("start");
        String end = request.getParameter("end");
        int pageSize = Integer.parseInt(request.getParameter("pageSize") == null ? "10" : request.getParameter("pageSize"));
        int pageNum = Integer.parseInt(request.getParameter("pageNum") == null ? "0" : request.getParameter("pageNum"));
        try {
            Criteria c = new Criteria();
            if (StringUtils.isNotBlank(key)) {
                //忽略大小写的模糊查询
                Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
                c.and("name").regex(pattern);
            }
            if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
                // 时间段
                long s = DateUtils.parseDate(start, SysContent.YYYY_MM_DD).getTime();
                long e = DateUtils.parseDate(end, SysContent.YYYY_MM_DD).getTime();
                c.and("date").lt(e).gt(s);
            }
            return mongoPageHelper.pageQuery(Query.query(c), SysRole.class, pageSize, pageNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResult<>();
    }

    @Override
    public void gotoAdd(Model model) {
        PrivilegeUtils.privilegesToTreeNode(model);
    }

    @Override
    public Object update(HttpServletRequest request) {
        return DataUtils.updateById(request, SysRole.class);
    }

    @Override
    public Object add(HttpServletRequest request) {
        String name = request.getParameter("name");
        String desc = request.getParameter("desc");
        String values = request.getParameter("values");

        //
        if (!roleRepository.existsByName(name)) {
            // 解析权限.不一定有的
            Set<String> privileges = new HashSet<>();

            if (!StringUtils.isEmpty(values)) {
                String[] ids = values.split(",");
                for (String id : ids) {
                    if (!StringUtils.isEmpty(id)) {
                        privileges.add(id);
                    }
                }
            }

            SysRole role = SysRole.builder()
                    .id(IDMarker.getInstance().nextId() + "")
                    .date(System.currentTimeMillis())
                    .name(name)
                    .desc(desc)
                    .state(1)
                    .privileges(privileges)
                    .build();

            roleRepository.save(role);
            return JsonResp.success();
        }
        return JsonResp.fail("角色名称已存在");
    }

    @Override
    public Object edit(HttpServletRequest request) {
        String id = request.getParameter("id");
        String desc = request.getParameter("desc");
        String values = request.getParameter("values");

        if (!StringUtils.isEmpty(id) && "0".equals(id)) {
            return JsonResp.fail("超级管理员的权限不允许修改");
        }

        SysRole role = roleRepository.findById(id).orElse(null);
        if (role != null) {
            //
            if (StringUtils.isEmpty(values)) {
                //说明没有修改任何权限
            } else {
                Set<String> privileges = new HashSet<>();
                String[] ids = values.split(",");
                for (String pid : ids) {
                    if (!StringUtils.isEmpty(pid)) {
                        privileges.add(pid);
                    }
                }
                role.setPrivileges(privileges);
            }
            role.setDesc(desc);

            roleRepository.save(role);
            return JsonResp.success();
        }
        return JsonResp.fail("角色名称不存在");
    }

    @Override
    public void gotoEdit(HttpServletRequest request, Model model) {
        String id = request.getParameter("id");
        SysRole role = roleRepository.findById(id).orElse(null);
        if (role != null && role.getPrivileges() != null && role.getPrivileges().size() > 0) {
            PrivilegeUtils.rolePrivilegesToTreeNode(model, role.getPrivileges());
        } else {
            PrivilegeUtils.privilegesToTreeNode(model);
        }
        model.addAttribute("role", role);
    }

    @Override
    public void gotoShow(HttpServletRequest request, Model model) {
        String id = request.getParameter("id");
        SysRole role = roleRepository.findById(id).orElse(null);
        if (role != null && role.getPrivileges() != null && role.getPrivileges().size() > 0) {
            PrivilegeUtils.rolePrivilegesToTreeNodeCheck(model, role.getPrivileges());
        } else {
            PrivilegeUtils.privilegesToTreeNode(model);
        }
        model.addAttribute("role", role);
    }

    @Override
    public Object delete(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (!StringUtils.isEmpty(id) && "0".equals(id)) {
            return JsonResp.fail("超级管理员角色不允许删除");
        }
        roleRepository.deleteById(id);
        return JsonResp.success();
    }
}
