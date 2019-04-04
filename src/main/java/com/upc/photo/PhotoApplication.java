package com.upc.photo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author waiter
 */
@CacheConfig(keyGenerator = "keyGenerator")
@EnableCaching(proxyTargetClass = true)
@EnableMongoAuditing
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableSpringDataWebSupport
@SpringBootApplication
public class PhotoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoApplication.class, args);
    }

}
