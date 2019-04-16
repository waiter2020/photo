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
import com.upc.photo.model.RestPage;
import com.upc.photo.service.PhotoService;
import com.upc.photo.utils.GetAddressByBaidu;
import net.coobird.thumbnailator.Thumbnails;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    @Caching(evict = {
            @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplgetAlbumPhoto-'+#result.album+'-*'", allEntries = true),
            @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplfindAll-'+authentication.principal.username+'-*'", allEntries = true)
    })
    @Override
    public void saveFile(byte[] bytes,Photo photo, String md5,Album album,String userName) {
        Photo.Location location = photo.getLocationInstance();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    //标签名
                    String tagName = tag.getTagName();
                    //标签信息
                    String desc = tag.getDescription();
                    if ("GPS Latitude".equals(tagName)) {

                        location.setLatitude(pointToLatlong(desc));
                    } else if ("GPS Longitude".equals(tagName)) {

                        location.setLongitude(pointToLatlong(desc));
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
            ObjectId store = gridFsTemplate.store(new ByteArrayInputStream(bytes), photo.getThumbnailName());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .scale(0.25f)
                    .toOutputStream(byteArrayOutputStream);
            ObjectId store1 = gridFsTemplate.store(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), photo.getThumbnailName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Photo save(Photo photo) {
        return photoDao.save(photo);
    }

    /**
           * 经纬度格式  转换为  度分秒格式 ,如果需要的话可以调用该方法进行转换
           * @param point 坐标点
           * @return
           */
     private static String pointToLatlong(String point) {
                 Double du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());
                 Double fen = Double.parseDouble(point.substring(point.indexOf("°")+1, point.indexOf("'")).trim());
                 Double miao = Double.parseDouble(point.substring(point.indexOf("'")+1, point.indexOf("\"")).trim());
                 Double duStr = du + fen / 60 + miao / 60 / 60 ;
                 return duStr.toString();
     }



    @Cacheable(cacheNames = "photos")
    @Override
    public ArrayList<Photo> findAll(String userName) {

        return photoDao.findAllByAuthor(userName);
    }

    @Cacheable(cacheNames = "photos")
    @Override
    public Page<Photo> findAll(String userName, Pageable pageable) {
        return new RestPage<>(photoDao.findAllByAuthorOrderByCreateDesc(userName, pageable));
    }

    @Cacheable(cacheNames = "photos")
    @Override
    public ArrayList<Photo> getAlbumPhoto(Album album) {
        return photoDao.findAllByAlbum(album);
    }

    @Cacheable(cacheNames = "photos")
    @Override
    public Page<Photo> getAlbumPhoto(Album album,Pageable pageable) {
        return new RestPage<>(photoDao.findAllByAlbumOrderByCreateDesc(album,pageable));
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
