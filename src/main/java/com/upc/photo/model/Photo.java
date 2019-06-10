package com.upc.photo.model;

import com.drew.metadata.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upc.photo.component.BigIntegerJsonDeSerializer;
import com.upc.photo.component.BigIntegerJsonSerializer;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: waiter
 * @Date: 2019/4/1 17:23
 * @Version 1.0
 */
@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo implements Serializable{

    @Id
    @Indexed
    @JsonSerialize(using = BigIntegerJsonSerializer.class)
    @JsonDeserialize(using = BigIntegerJsonDeSerializer.class)
    private BigInteger id;

    @CreatedBy
    private String author;

    /**
     * 原文件名
     */
    private String name;

    /***
     * 服务端存储的文件名
     */
    private String fileName;

    /**
     * 缩略图文件名
     */
    private String thumbnailName;


    @DBRef
    private Album album;

    private List<String> type;

    private Location location;
    private Address address;
    private Date create;
    private Long size;
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date upload;
    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modify;


    @DBRef
    private List<Face> faces;

    @JsonIgnore
    public Location getLocationInstance(){
        return new Location();
    }



    @Data
    public static class Location implements Serializable {
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

