package com.upc.photo.model;

import com.drew.metadata.Tag;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upc.photo.component.BigIntegerJsonSerializer;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: waiter
 * @Date: 2019/4/1 17:23
 * @Version 1.0
 */
@Data
@Document
public class Photo implements Serializable{

    @Indexed
    @JsonSerialize(using = BigIntegerJsonSerializer.class)
    private BigInteger id;
    @CreatedBy
    private String author;

    private String name;

    private String fileName;

    @DBRef
    private Album album;

    private PhotoType type;

    private Location location;
    private Address address;
    private Date create;
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date upload;
    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modify;

    public Location getLocationInstance(){
        return new Location();
    }

    @Data
    public class Location implements Serializable {
        /**
         *  纬度
         *  */
        String latitude;
        /**
         * 经度
         */
        String longitude;
    }


}

