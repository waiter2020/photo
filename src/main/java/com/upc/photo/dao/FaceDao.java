package com.upc.photo.dao;

import com.upc.photo.model.Face;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @Author: waiter
 * @Date: 2019/5/27 11:53
 * @Version 1.0
 */

public interface FaceDao extends MongoRepository<Face, BigInteger> {
    ArrayList<Face> findAllByAuthor(String username);

}
