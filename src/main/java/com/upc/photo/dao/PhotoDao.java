package com.upc.photo.dao;

import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @Author: waiter
 * @Date: 2019/4/1 21:23
 * @Version 1.0
 */

public interface PhotoDao extends MongoRepository<Photo, BigInteger> {

    Long countAllByAuthorAndAddress_City(String userName,String cityName);
    Photo findTopByAuthorAndAddress_City(String cityName);

    ArrayList<Photo> findAllByAuthor(String author);
    ArrayList<Photo> findAllByAlbum(Album album);
    Page<Photo> findAllByAuthorOrderByCreateDesc(String author, Pageable pageable);
    Page<Photo> findAllByAlbumOrderByCreateDesc(Album album, Pageable pageable);
}
