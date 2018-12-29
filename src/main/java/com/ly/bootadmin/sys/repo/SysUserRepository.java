package com.ly.bootadmin.sys.repo;

import com.ly.bootadmin.sys.bean.SysUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author linyun
 * @date 2018/11/11 00:06
 */
@Repository
public interface SysUserRepository extends MongoRepository<SysUser, String> {
    SysUser findByNameAndPass(String username, String password);

    long countByName(String username);

    SysUser findByName(String username);
}
