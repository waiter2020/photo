package com.upc.photo.controller;

import com.upc.photo.model.Role;
import com.upc.photo.model.User;
import com.upc.photo.service.RoleService;
import com.upc.photo.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @Author: waiter
 * @Date: 2019/4/3 17:47
 * @Version 1.0
 */

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @ApiOperation("创建用户")
    @PostMapping("/add")
    public User addUser(User user){
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(roleService.findOne("ROLE_USER"));
        user.setAuthorities(roles);
        return userService.createUser(user);
    }

    @ApiOperation("修改昵称")
    @PostMapping("/change_nick_name")
    public User changeNickName(String nickName,Authentication authentication){
        User user = (User) authentication.getPrincipal();
        user.setNickname(nickName);
        return userService.save(user);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/get_info")
    public User getUserInfo(Authentication authentication){
        return (User) authentication.getPrincipal();
    }
}
