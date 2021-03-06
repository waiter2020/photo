package com.upc.photo.service;

import com.drew.imaging.ImageProcessingException;
import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * @Author: waiter
 * @Date: 2019/4/3 13:38
 * @Version 1.0
 */

public interface PhotoService {



    Photo findOneByCity(String userName, String cityName);

    Photo findOneByDistrict(String username, String district);

    Photo findOneByProvince(String username, String province);

    Photo findOneByType(String userName,String type);

    Long countByCity(String userName,String cityName);

    Page<Photo> getByDistrict(String username, String district, PageRequest of);

    Page<Photo> getByProvince(String username, String province, PageRequest of);

    Set<String> getCityList(String userName);

    Map<String, Long> getDistrictList(String username);



    Map<String, Long> getProvinceList(String username);

    Map<String,Long> getTypeList(String userName);

    void delete(Photo photo);

    /**
     * @param photo
     * @param photo
     * @return
     */
    void saveFile(byte[] bytes, Photo photo, String md5) throws IOException, ImageProcessingException;

    Photo save(Photo photo);

    void saveAll(Iterable<Photo> iterable);






    ArrayList<Photo> findAll(String userName);

    ArrayList<Photo> getAlbumPhoto(Album album);

    Photo findById(BigInteger id);

    GridFsResource getPhotoResource(String fileName);

    Photo changeToAlbum(Photo photo);

    Page<Photo> findAll(String userName, Pageable pageable);
    Page<Photo> getAlbumPhoto(Album album,Pageable pageable);

    Page<Photo> getByCity(String userName,String cityName,Pageable pageable);

    Page<Photo> getByType(String userName,String type,Pageable pageable);

    void sync();
}
