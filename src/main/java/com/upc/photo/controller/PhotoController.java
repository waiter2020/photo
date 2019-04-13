package com.upc.photo.controller;

import com.drew.imaging.ImageProcessingException;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;

import com.upc.photo.model.PhotoType;
import com.upc.photo.service.PhotoService;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

import com.mongodb.client.gridfs.GridFSBuckets;

import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @Author: waiter
 * @Date: 2019/4/1 20:57
 * @Version 1.0
 * 照片
 */

@RestController
@RequestMapping("/photo")
public class PhotoController {

    private final GridFsTemplate gridFsTemplate;

    private final PhotoService photoService;


    public PhotoController(GridFsTemplate gridFsTemplate,  PhotoService photoService) {
        this.gridFsTemplate = gridFsTemplate;
        this.photoService = photoService;
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile  file,
                         @RequestParam(name = "md5",required = false) String md5) throws IOException, ImageProcessingException {
        //TODO: MD5校验


        return photoService.save(file);
    }

    /**
     * 获取一张照片
     * @param photo
     *
     * @return
     * @throws IOException
     */
    @PreAuthorize("#photo.author==authentication.principal.username or hasAuthority('ADMIN')")
    @Cacheable(cacheNames = "photo_file")
    @GetMapping("/get/{photoId}")
    public ResponseEntity<InputStreamResource> getPhoto(@PathVariable("photoId") Photo photo) throws IOException {
        return getPhotoFile(photo);
    }




    /**
     * 获取当前用户所有照片
     * @param authentication
     * @return
     */
    @GetMapping("/get_all")
    public ArrayList<Photo> getAllPhoto(Authentication authentication){
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return photoService.findAll(username);
    }

    /**
     * 获取某一相册所有照片
     * @param album
     * @return
     */
    @PreAuthorize("#album.author==authentication.principal.username or hasAuthority('ADMIN')")
    @GetMapping("/get_album_photos/{id}")
    public ArrayList<Photo> getAlbumPhotos(@PathVariable("id")Album album){
        return photoService.getAlbumPhoto(album);
    }


    private ResponseEntity<InputStreamResource> getPhotoFile(@NotNull Photo photo) throws IOException {
        GridFSFile myFile = gridFsTemplate.findOne(query(whereFilename().is(photo.getFileName())));
        if (myFile==null){
            return null;
        }
        GridFsResource gridFsResource=gridFsTemplate.getResource(myFile);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "cache, store, must-revalidate");
        headers.add("Content-Disposition", "attachment; fileName="+  photo.getName() +";filename*=utf-8''"+ URLEncoder.encode(photo.getName(),"UTF-8"));
        headers.add("Pragma", "cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(myFile.getLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(gridFsResource.getInputStream()));
    }
}
