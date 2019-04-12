package com.upc.photo.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.upc.photo.dao.PhotoDao;
import com.upc.photo.model.Photo;
import com.upc.photo.model.PhotoType;
import com.upc.photo.service.PhotoService;
import com.upc.photo.utils.GetAddressByBaidu;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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


    @Override
    public Photo save(MultipartFile file) throws IOException, ImageProcessingException {
        UUID uuid = UUID.randomUUID();
        Photo photo = new Photo();
        photo.setName(file.getOriginalFilename());
        photo.setFileName(uuid + "-" + file.getOriginalFilename());

        Photo.Location location = photo.getLocationInstance();

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

        // query the tag's value
        Date date
                = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

        photo.setCreate(date);

        photo.setAddress(GetAddressByBaidu.getAddress(location.getLatitude(),location.getLongitude()));

        //TODO:调用py接口获取照片类别
        photo.setType(PhotoType.SCENERY);
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), uuid + "-" + file.getOriginalFilename());
        if (fileId == null) {
            throw new IOException();
        }
        return photoDao.save(photo);
    }

    @Cacheable(cacheNames = "photo")
    @Override
    public ArrayList<Photo> findAll(String userName) {
        return photoDao.findAllByAuthor(userName);
    }
}
