package com.upc.photo.dao;

import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    ArrayList<Photo> findAllByAuthorAndAddress_CityNot(String userName,String city);

    Long countAllByAuthorAndAddress_City(String userName,String cityName);

    Long countAllByAuthorAndTypeContaining(String userName,String type);

    Page<Photo> findAllByAuthorAndAddress_DistrictOrderByUploadDesc(String username, String district, PageRequest of);

    Page<Photo> findAllByAuthorAndAddress_ProvinceOrderByUploadDesc(String username, String province, PageRequest of);

    Photo findTopByAuthorAndAddress_City(String userName,String cityName);

    Photo findTopByAuthorAndAddress_District(String username, String district);

    Photo findTopByAuthorAndAddress_Province(String username, String province);

    Photo findTopByAuthorAndTypeContaining(String userName,String type);

    ArrayList<Photo> findAllByAuthor(String author);
    ArrayList<Photo> findAllByAlbum(Album album);
    Page<Photo> findAllByAuthorOrderByUploadDesc(String author, Pageable pageable);
    Page<Photo> findAllByAlbumOrderByUploadDesc(Album album, Pageable pageable);
    Page<Photo> findAllByAuthorAndAddress_CityOrderByUploadDesc(String author,String cityName, Pageable pageable);
    Page<Photo> findAllByAuthorAndTypeContainingOrderByUploadDesc(String author,String type, Pageable pageable);
}
