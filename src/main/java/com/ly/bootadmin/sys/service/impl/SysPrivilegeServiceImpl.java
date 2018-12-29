package com.ly.bootadmin.sys.service.impl;

import com.ly.bootadmin.security.ShiroService;
import com.ly.bootadmin.sys.bean.SysPrivilege;
import com.ly.bootadmin.sys.bean.SysRole;
import com.ly.bootadmin.sys.repo.SysPrivilegeRepository;
import com.ly.bootadmin.sys.repo.SysRoleRepository;
import com.ly.bootadmin.sys.service.ISysPrivilegeService;
import com.ly.bootadmin.sys.utils.PrivilegeUtils;
import com.ly.bootadmin.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author linyun
 * @date 2018/11/20 12:01
 */
@Slf4j
@Service
public class SysPrivilegeServiceImpl implements ISysPrivilegeService {

    private final SysPrivilegeRepository privilegeRepository;

    private final MongoPageHelper mongoPageHelper;

    private final SysRoleRepository roleRepository;

    private final ShiroService shiroService;

    private final MongoTemplate template;

    @Autowired
    public SysPrivilegeServiceImpl(SysPrivilegeRepository privilegeRepository, MongoPageHelper mongoPageHelper, SysRoleRepository roleRepository, ShiroService shiroService, MongoTemplate template) {
        this.privilegeRepository = privilegeRepository;
        this.mongoPageHelper = mongoPageHelper;
        this.roleRepository = roleRepository;
        this.shiroService = shiroService;
        this.template = template;
    }


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
                c.orOperator(
                        Criteria.where("name").regex(pattern),
                        Criteria.where("desc").regex(pattern),
                        Criteria.where("uri").regex(pattern)
                );
            }
            if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
                // 时间段
                long s = DateUtils.parseDate(start, SysContent.YYYY_MM_DD).getTime();
                long e = DateUtils.parseDate(end, SysContent.YYYY_MM_DD).getTime();
                c.and("date").lt(e).gt(s);
            }
            return mongoPageHelper.pageQuery(Query.query(c), SysPrivilege.class, pageSize, pageNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResult<>();
    }

    @Override
    public Object add(HttpServletRequest request) {
        String name = request.getParameter("name");
        String desc = request.getParameter("desc");
        String uri = request.getParameter("uri");
        if (!privilegeRepository.existsByUri(uri)) {
            SysPrivilege privilege = SysPrivilege.builder()
                    .id(IDMarker.getInstance().nextId() + "")
                    .date(System.currentTimeMillis())
                    .state(1)
                    .pid(null)
                    .uri(uri)
                    .desc(desc)
                    .name(name)
                    .build();
            privilegeRepository.save(privilege);
        } else {
            return JsonResp.fail("权限已存在");
        }
        return JsonResp.success();
    }

    @Override
    public Object refreshAll(HttpServletRequest request) {
        shiroService.updatePrivileges();
        return JsonResp.success();
    }

    @Override
    public Object reloadAllPrivileges(HttpServletRequest request) {
        WebApplicationContext wc = (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        RequestMappingHandlerMapping bean = wc.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
        Set<String> result = new HashSet<>();
        for (RequestMappingInfo rmi : handlerMethods.keySet()) {
            PatternsRequestCondition pc = rmi.getPatternsCondition();
            Set<String> pSet = pc.getPatterns();
            result.addAll(pSet);
        }
        if (result.size() > 0) {
            Set<String> uris = new HashSet<>();
            PrivilegeUtils.addPrivilegesToDataBase(result, uris);
            if (uris.size() > 0) {
                // 找到超级管理员角色, 加入到超级管理员的角色中
                SysRole role = roleRepository.findById("0").orElse(null);

                // 找到所有的权限, 不论是否生效
                List<SysPrivilege> privileges = privilegeRepository.findAll();
                Set<String> allPrivileges = new HashSet<>();
                for (SysPrivilege privilege : privileges) {
                    allPrivileges.add(privilege.getId());
                }
                if (role == null) {
                    //没有超级管理员角色了,创建
                    role = SysRole.builder()
                            .id("0")
                            .date(System.currentTimeMillis())
                            .name("ROLE_SUPERADMIN")
                            .desc("超级管理员")
                            .state(1)
                            .privileges(allPrivileges)
                            .build();
                }else {
                    role.setPrivileges(allPrivileges);
                }
                roleRepository.save(role);
            }
        }
        return JsonResp.success();
    }

    @Override
    public void gotoRelationShip(Model model) {
        //1. 先查询没有父级的
        PrivilegeUtils.privilegesToTreeNode(model);
    }

    @Override
    public Object relationship(HttpServletRequest request) {
        String id = request.getParameter("id");
        String value = request.getParameter("value");
        String[] values = value.split(",");
        Set<String> ids = new HashSet<>();
        for (String v : values) {
            if(!StringUtils.isEmpty(v)){
                ids.add(v);
            }
        }
        Update u = new Update();
        u.set("pid", id);
        template.updateMulti(Query.query(Criteria.where("_id").in(ids)), u, SysPrivilege.class);
        return JsonResp.success();
    }

    @Override
    public Object update(HttpServletRequest request) {
        return DataUtils.updateById(request, SysPrivilege.class);
    }

}
