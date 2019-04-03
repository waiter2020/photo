package com.upc.photo.dao;

import com.upc.photo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: waiter
 * @Date: 2019/4/3 17:18
 * @Version 1.0
 */

public interface UserDao extends JpaRepository<User,Long> {
    User findByUsername(String userName);
}
