package com.upc.photo.service.impl;

import com.upc.photo.dao.RoleDao;
import com.upc.photo.model.Role;
import com.upc.photo.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * @Author: waiter
 * @Date: 2019/4/3 17:30
 * @Version 1.0
 */
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }


    @Override
    public Role save(Role role) {
        return roleDao.save(role);
    }

    @Override
    public Role findOne(String role) {
        return roleDao.findByRole(role);
    }
}
