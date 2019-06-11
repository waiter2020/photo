package com.upc.photo.dao;

import com.upc.photo.model.Face;
import com.upc.photo.model.FaceGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @Author: waiter
 * @Date: 2019/5/28 20:22
 * @Version 1.0
 */

public interface FaceGroupDao extends MongoRepository<FaceGroup, BigInteger> {
    ArrayList<FaceGroup> findAllByAuthor(String author);
    void deleteAllByAuthor(String username);
}
