package com.upc.photo.dao;

import com.upc.photo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: waiter
 * @Date: 2019/4/3 17:28
 * @Version 1.0
 */

public interface RoleDao extends JpaRepository<Role,Integer> {
    Role findByRole(String role);
}
