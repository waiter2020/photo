package com.upc.photo.service;

import com.upc.photo.model.Photo;
import com.upc.photo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/4/3 13:14
 * @Version 1.0
 */

public interface UserService extends UserDetailsService {


    void deleteUserLoginInfo(String username);

    /**
     * @param username
     * @return
     */
    User getUserLoginInfo(String username);

    /**
     * @param principal
     * @return
     */
    String saveUserLoginInfo(UserDetails principal);

    User createUser(User user);

    User save(User user);

    List<User> findAll();

}
