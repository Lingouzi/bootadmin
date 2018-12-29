package com.ly.bootadmin.sys.repo;

import com.ly.bootadmin.sys.bean.SysRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author linyun
 * @date 2018/11/11 00:06
 */
@Repository
public interface SysRoleRepository extends MongoRepository<SysRole,String> {
    boolean existsByName(String name);
}
