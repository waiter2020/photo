package com.upc.photo.dao;

import com.upc.photo.model.Album;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

/**
 * @Author: waiter
 * @Date: 2019/4/12 17:08
 * @Version 1.0
 */

public interface AlbumDao extends MongoRepository<Album, BigInteger> {
}
