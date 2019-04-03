package com.upc.photo.service;

import com.upc.photo.model.Role;

/**
 * @Author: waiter
 * @Date: 2019/4/3 17:30
 * @Version 1.0
 */

public interface RoleService {
    Role save(Role role);
    Role findOne(String role);
}
