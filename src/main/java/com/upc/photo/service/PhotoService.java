package com.upc.photo.service;

import com.drew.imaging.ImageProcessingException;
import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    Photo save(MultipartFile photo) throws IOException, ImageProcessingException;

    ArrayList<Photo> findAll(String userName);

    ArrayList<Photo> getAlbumPhoto(Album album);
}
