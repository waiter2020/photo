package com.upc.photo.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.upc.photo.dao.UserDao;
import com.upc.photo.model.User;
import com.upc.photo.service.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: waiter
 * @Date: 2019/4/2 20:32
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements  UserService {

    private final RedisTemplate redisTemplate;
    private PasswordEncoder passwordEncoder;
    private final UserDao userDao;
    private final RedisCacheManager cacheManager;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public UserServiceImpl(RedisTemplate redisTemplate, UserDao userDao, RedisCacheManager cacheManager) {
        this.redisTemplate = redisTemplate;
        this.userDao = userDao;
        this.cacheManager = cacheManager;
        //默认使用 bcrypt， strength=10
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public UserDetails getUserLoginInfo(String username) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        String salt = (String) operations.get("token:"+username);
        Cache cache = cacheManager.getCache("user");
        User user = cache.get("com.upc.photo.service.impl.UserServiceImpl-loadUserByUsername-" + username, User.class);
        if (user==null) {
            user = (User) loadUserByUsername(username);
        }

        //将salt放到password字段返回
        user.setPassword(salt);
        return user;
    }

    @Override
    public String saveUserLoginInfo(UserDetails user) {
        String salt = BCrypt.gensalt();
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        operations.set("token:"+user.getUsername(), salt);
        redisTemplate.expire("token:" + user.getUsername(), 3600, TimeUnit.SECONDS);

        Algorithm algorithm = Algorithm.HMAC256(salt);
        //设置1小时后过期
        Date date = new Date(System.currentTimeMillis()+3600*1000);
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    @Cacheable(cacheNames = "user")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user==null){
            user = new User();
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
       return userDao.save(user);
    }

    @CacheEvict(cacheNames = "user",key = "'com.upc.photo.service.impl.UserServiceImpl-loadUserByUsername-'+#user.username")
    @Override
    public User save(User user) {
        return userDao.save(user);
    }

    @Override
    public void deleteUserLoginInfo(String username) {
        /*
           清除数据库或者缓存中登录salt
         */
        redisTemplate.delete("token:"+username);
    }
}
