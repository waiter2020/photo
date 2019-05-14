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

    ArrayList<Photo> findAllByAuthorAndAddressNotNull(String userName);

    Long countAllByAuthorAndAddress_City(String userName,String cityName);

    Long countAllByAuthorAndType(String userName,String type);

    Photo findTopByAuthorAndAddress_City(String userName,String cityName);

    Photo findTopByAuthorAndType(String userName,String type);

    ArrayList<Photo> findAllByAuthor(String author);
    ArrayList<Photo> findAllByAlbum(Album album);
    Page<Photo> findAllByAuthorOrderByCreateAsc(String author, Pageable pageable);
    Page<Photo> findAllByAlbumOrderByCreateAsc(Album album, Pageable pageable);
    Page<Photo> findAllByAuthorAndAddress_CityOrderByCreateAsc(String author,String cityName, Pageable pageable);
    Page<Photo> findAllByAuthorAndTypeContainingOrderByCreateAsc(String author,String type, Pageable pageable);
}
