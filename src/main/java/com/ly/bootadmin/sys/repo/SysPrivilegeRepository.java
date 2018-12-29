package com.ly.bootadmin.sys.repo;

import com.ly.bootadmin.sys.bean.SysPrivilege;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author linyun
 * @date 2018/11/11 00:06
 */
@Repository
public interface SysPrivilegeRepository extends MongoRepository<SysPrivilege,String> {
    long countByUri(String uri);

    boolean existsByUri(String uri);

    List<SysPrivilege> findByPid(String id);

    List<SysPrivilege> findByState(int i);
}
