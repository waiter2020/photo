package com.upc.photo.service;

import com.drew.imaging.ImageProcessingException;
import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * @Author: waiter
 * @Date: 2019/4/3 13:38
 * @Version 1.0
 */

public interface PhotoService {
    /**
     * @param photo
     * @return
     */
    void save(MultipartFile photo,String md5,Album album,String userName) throws IOException, ImageProcessingException;

    ArrayList<Photo> findAll(String userName);

    ArrayList<Photo> getAlbumPhoto(Album album);

    Photo findById(BigInteger id);

    GridFsResource getPhotoResource(String fileName);


    Page<Photo> findAll(String userName, Pageable pageable);
    Page<Photo> getAlbumPhoto(Album album,Pageable pageable);
}
