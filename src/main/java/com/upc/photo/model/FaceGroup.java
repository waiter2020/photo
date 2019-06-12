package com.upc.photo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: waiter
 * @Date: 2019/5/28 19:15
 * @Version 1.0
 */
@Document
@Data
public class FaceGroup {
    @Id
    @Indexed
    @JsonSerialize(using = BigIntegerJsonSerializer.class)
    @JsonDeserialize(using = BigIntegerJsonDeSerializer.class)
    private BigInteger id;

    private String name;


    private String face;



    private Map<String,String> faces;

    @CreatedBy
    private String author;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date create;
    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modify;

}
