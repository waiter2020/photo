package com.upc.photo.service.impl;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.upc.photo.dao.UserDao;
import com.upc.photo.model.User;
import com.upc.photo.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: waiter
 * @Date: 2019/4/2 20:32
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements  UserService {

    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserDao userDao;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public UserServiceImpl(RedisTemplate redisTemplate, PasswordEncoder passwordEncoder, UserDao userDao) {
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
    }


    @Override
    public User getUserLoginInfo(String username) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        String salt = (String) operations.get("token:"+username);

        User user = (User) loadUserByUsername(username);


        //将salt放到password字段返回
        user.setPassword(salt);
        return user;
    }

    @Override
    public String saveUserLoginInfo(UserDetails user) {
        String salt = BCrypt.gensalt();
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        operations.set("token:"+user.getUsername(), salt);
        redisTemplate.expire("token:" + user.getUsername(), 1800, TimeUnit.SECONDS);

        Algorithm algorithm = Algorithm.HMAC256(salt);
        //设置1小时后过期
        Date date = new Date(System.currentTimeMillis()+3600*1000);
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getCache("user:" + username);

        if (user==null) {
            user = userDao.findByUsername(username);
        }

        if (user!=null){
            setCache("user:"+username,user);
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
       return userDao.save(user);
    }

    private void removeCache(String key){
        redisTemplate.delete(key);
    }

    private void setCache(String key,User user){
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        operations.set(key, JSON.toJSONString(user));
        redisTemplate.expire(key, 1800, TimeUnit.SECONDS);
    }


    private User getCache(String key){
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        String src = (String) operations.get(key);
        return JSON.parseObject(src,User.class);
    }

    @Override
    public User save(User user) {
        removeCache("user:"+user.getUsername());
        return userDao.save(user);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }


    @Override
    public void deleteUserLoginInfo(String username) {
        /*
           清除数据库或者缓存中登录salt
         */
        redisTemplate.delete("token:"+username);
    }
}
