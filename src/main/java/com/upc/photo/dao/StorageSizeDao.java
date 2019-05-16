package com.upc.photo.dao;

import com.upc.photo.model.Album;
import com.upc.photo.model.StorageSize;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @Author: waiter
 * @Date: 2019/4/12 17:08
 * @Version 1.0
 */

public interface StorageSizeDao extends MongoRepository<StorageSize, BigInteger> {
    ArrayList<StorageSize> getFirstByAuthor(String author);
}
