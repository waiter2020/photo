package com.upc.photo.controller;

import com.upc.photo.model.Role;
import com.upc.photo.model.User;
import com.upc.photo.service.RoleService;
import com.upc.photo.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @Author: waiter
 * @Date: 2019/4/3 17:47
 * @Version 1.0
 */

@RestController
@RequestMapping("/user")
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RoleService roleService;


    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
    public User changeNickName(@RequestParam("nickName") String nickName, Authentication authentication){
        User user = (User) userService.loadUserByUsername(((User) authentication.getPrincipal()).getUsername());
        user.setNickname(nickName);
        return userService.save(user);
    }

    @ApiOperation("修改密码")
    @PostMapping("/change_password")
    public User changePassword(@RequestParam("password") String password, Authentication authentication){
        User user = (User) userService.loadUserByUsername(((User) authentication.getPrincipal()).getUsername());
        user.setPassword(passwordEncoder.encode(password         ));
        return userService.save(user);
    }


    @ApiOperation(value = "获取隐私空间令牌",notes = "此令牌注意保存，后面访问隐私空间内数据需要使用")
    @PostMapping("/get_security")
    public String getSecurity(@RequestParam("password") String password, Authentication authentication){
        User user = (User) userService.loadUserByUsername(((User) authentication.getPrincipal()).getUsername());
        if (passwordEncoder.matches(password,user.getSecurityPassword())){
            return user.getSecurityToken();
        }
        return null;
    }


    @ApiOperation("修改隐私空间密码")
    @PostMapping("/change_security_password")
    public User changeSecurityPassword(@RequestParam("password") String password, Authentication authentication){
        User user = (User) userService.loadUserByUsername(((User) authentication.getPrincipal()).getUsername());
        user.setSecurityPassword(passwordEncoder.encode(password));
        if (user.getSecurityToken()==null|| "".equals(user.getSecurityToken())){
            user.setSecurityToken(passwordEncoder.encode(user.getUsername()+password+UUID.randomUUID()));
        }
        return userService.save(user);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/get_info")
    public User getUserInfo(Authentication authentication){
        return (User) authentication.getPrincipal();
    }
}
