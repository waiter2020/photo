package com.upc.photo.model;

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

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: waiter
 * @Date: 2019/4/1 17:23
 * @Version 1.0
 */
@Data
@Document
public class Photo {

    @Indexed
    @JsonSerialize(using = BigIntegerJsonSerializer.class)
    private BigInteger id;
    @CreatedBy
    private String author;

    private String name;

    private String fileName;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date create;
    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modify;

}
