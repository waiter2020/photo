package com.upc.photo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upc.photo.component.BigIntegerJsonDeSerializer;
import com.upc.photo.component.BigIntegerJsonSerializer;
import lombok.Data;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @Author: waiter
 * @Date: 2019/5/27 11:38
 * @Version 1.0
 */
@Document
@Data
public class Face implements Serializable {
    @Id
    @Indexed
    @JsonSerialize(using = BigIntegerJsonSerializer.class)
    @JsonDeserialize(using = BigIntegerJsonDeSerializer.class)
    private BigInteger id;

    @JsonSerialize(using = BigIntegerJsonSerializer.class)
    @JsonDeserialize(using = BigIntegerJsonDeSerializer.class)
    private BigInteger photoId;

    private String groupId;

    private String name;


    @JsonIgnore
    private byte[] bytes;

    @JsonIgnore
    private double[][] matrix;

    @CreatedBy
    private String author;
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date create;
    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modify;

}
