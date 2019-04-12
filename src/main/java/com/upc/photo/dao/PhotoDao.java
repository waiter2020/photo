package com.upc.photo.dao;

import com.upc.photo.model.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @Author: waiter
 * @Date: 2019/4/1 21:23
 * @Version 1.0
 */

public interface PhotoDao extends MongoRepository<Photo, BigInteger> {
    ArrayList<Photo> findAllByAuthor(String author);
}
