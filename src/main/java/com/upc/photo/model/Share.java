package com.upc.photo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upc.photo.component.BigIntegerJsonDeSerializer;
import com.upc.photo.component.BigIntegerJsonSerializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/5/20 10:54
 * @Version 1.0
 */

@Data
@RedisHash("share")
public class Share implements Serializable {
    @Id
    private String id;
    @Indexed
    private String author;

    /**
     * 分享类型：0为相册，1为照片
     */
    private Integer type;

    /**
     * 分享的相册或照片的id列表
     */
    private List<BigInteger> shareList;

    /**
     * 加密分享的密码
     */
    private String password;

    /**
     * 分享有效期-1为永久
     * 其它按秒计算，并且每隔1s -1
     */
    @TimeToLive
    private Long expiration;

    public Share() {
    }
}
