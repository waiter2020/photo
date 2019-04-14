package com.upc.photo.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.upc.photo.dao.PhotoDao;
import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;
import com.upc.photo.model.PhotoType;
import com.upc.photo.service.PhotoService;
import com.upc.photo.utils.GetAddressByBaidu;
import net.coobird.thumbnailator.Thumbnails;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

/**
 * @Author: waiter
 * @Date: 2019/4/1 21:32
 * @Version 1.0
 */
@Service
public class PhotoServiceImpl implements PhotoService {
    private final PhotoDao photoDao;
    private final GridFsTemplate gridFsTemplate;

    public PhotoServiceImpl(PhotoDao photoDao, GridFsTemplate gridFsTemplate) {
        this.photoDao = photoDao;
        this.gridFsTemplate = gridFsTemplate;
    }


    @Async
    @Caching(evict = {
            @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplgetAlbumPhoto'+#result.album", allEntries = true),
            @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplfindAll'+authentication.principal.username", allEntries = true)
    })
    @Override
    public void save(MultipartFile file, String md5,Album album,String userName) {

        save(file,album,userName);
    }


    private void save(MultipartFile file,Album album,String userName) {
        UUID uuid = UUID.randomUUID();
        Photo photo = new Photo();
        photo.setName(file.getOriginalFilename());
        photo.setFileName(uuid + "-" + file.getOriginalFilename());
        photo.setAlbum(album);
        photo.setThumbnailName(uuid + "-" + "thumbnail" + "-" + file.getOriginalFilename());
        photo.setAuthor(userName);

        Photo.Location location = photo.getLocationInstance();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    //标签名
                    String tagName = tag.getTagName();
                    //标签信息
                    String desc = tag.getDescription();
                    if ("GPS Latitude".equals(tagName)) {

                        location.setLatitude(desc);
                    } else if ("GPS Longitude".equals(tagName)) {

                        location.setLongitude(desc);
                    }
                }
            }

            photo.setLocation(location);

            // obtain the Exif directory
            ExifSubIFDDirectory directory
                    = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (directory!=null) {
                // query the tag's value
                Date date
                        = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                photo.setCreate(date);

            }
            photo.setAddress(GetAddressByBaidu.getAddress(location.getLatitude(), location.getLongitude()));

            //TODO:调用py接口获取照片类别
            photo.setType(PhotoType.SCENERY);
            gridFsTemplate.store(file.getInputStream(), uuid + "-" + file.getOriginalFilename());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .scale(0.25f)
                    .toOutputStream(byteArrayOutputStream);
            gridFsTemplate.store(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), photo.getThumbnailName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        photoDao.save(photo);
    }


    @Cacheable(cacheNames = "photos")
    @Override
    public ArrayList<Photo> findAll(String userName) {
        return photoDao.findAllByAuthor(userName);
    }

    @Cacheable(cacheNames = "photos")
    @Override
    public ArrayList<Photo> getAlbumPhoto(Album album) {
        return photoDao.findAllByAlbum(album);
    }

    @Override
    public Photo findById(BigInteger id) {
        Optional<Photo> byId = photoDao.findById(id);

        return photoDao.findById(id).get();
    }


    @Override
    public GridFsResource getPhotoResource(String fileName) {
        GridFSFile myFile = gridFsTemplate.findOne(query(whereFilename().is(fileName)));
        if (myFile==null){
            return null;
        }
       return gridFsTemplate.getResource(myFile);

    }
}
