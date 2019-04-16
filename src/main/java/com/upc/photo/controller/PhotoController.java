package com.upc.photo.controller;

import com.drew.imaging.ImageProcessingException;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;

import com.upc.photo.model.PhotoType;
import com.upc.photo.service.PhotoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

import com.mongodb.client.gridfs.GridFSBuckets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import java.io.*;
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
@Api
public class PhotoController {

    private final GridFsTemplate gridFsTemplate;

    private final PhotoService photoService;


    public PhotoController(GridFsTemplate gridFsTemplate,  PhotoService photoService) {
        this.gridFsTemplate = gridFsTemplate;
        this.photoService = photoService;
    }

    @ApiOperation("上传图片")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/upload")
    public Photo upload(@RequestParam("file") MultipartFile  file,
                         @RequestParam(name = "md5",required = false) String md5,
                          @RequestParam(name = "id",required = false)Album album,
                          Authentication authentication) throws IOException, ImageProcessingException {
        //TODO: MD5校验
        UUID uuid = UUID.randomUUID();
        Photo photo = new Photo();
        photo.setName(file.getOriginalFilename());
        photo.setFileName(uuid + "-" + file.getOriginalFilename());
        photo.setAlbum(album);
        photo.setThumbnailName(uuid + "-" + "thumbnail" + "-" + file.getOriginalFilename());
        photoService.saveFile(inputStreamConvertToByteArray(file.getInputStream()),photo,md5,album,((UserDetails) authentication.getPrincipal()).getUsername());
        return photoService.save(photo);
    }

    /**
     * 把一个文件转化为byte字节数组。
     *
     * @return
     */
    private byte[] inputStreamConvertToByteArray(InputStream fis) {
        byte[] data = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();

            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }


    /**
     * 获取一张照片(原图)
     * @param photo
     *
     * @return
     * @throws IOException
     */
    @ApiOperation("获取原始图片")
    @PreAuthorize("#photo.author==authentication.principal.username or hasAuthority('ADMIN')")
    @GetMapping("/get_photo/{id}")
    public ResponseEntity<InputStreamResource> getPhoto(@PathVariable("id") Photo photo) throws IOException {
        return getPhotoFile(photo.getFileName(),photo.getName());
    }

    /**
     * 获取一张照片(缩略图)
     * @param photo
     *
     * @return
     * @throws IOException
     */
    @ApiOperation("获取缩略图")
    @PreAuthorize("#photo.author==authentication.principal.username or hasAuthority('ADMIN')")
    @GetMapping("/get_thumbnail_photo/{id}")
    public ResponseEntity<InputStreamResource> getThumbnailPhoto(@PathVariable("id") Photo photo) throws IOException {

        return getPhotoFile(photo.getThumbnailName(),photo.getName());
    }


    /**
     * 获取当前用户所有照片
     * @param authentication
     * @return
     */
    @ApiOperation("获取用户所有照片")
    @GetMapping({"/get_all/{page}/{pageSize}","/get_all","/get_all/{page}"})
    public Page<Photo> getAllPhoto(Authentication authentication,
                                   @PathVariable(value = "page",required = false)Integer page,
                                   @PathVariable(value = "pageSize",required = false)Integer pageSize ){
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        if (page==null){
            page=0;
        }
        if (pageSize==null){
            pageSize=20;
        }

        return photoService.findAll(username,PageRequest.of(page,pageSize));
    }

    /**
     * 获取某一相册所有照片
     * @param album
     * @return
     */
    @ApiOperation("获取相册所有照片")
    @PreAuthorize("#album.author==authentication.principal.username or hasAuthority('ADMIN')")
    @GetMapping({"/get_album_photos/{id}","/get_album_photos/{id}/{page}","/get_album_photos/{id}/{page}/{pageSize}"})
    public Page<Photo> getAlbumPhotos(@PathVariable("id")Album album,
                                           @PathVariable(value = "page",required = false)Integer page,
                                           @PathVariable(value = "pageSize",required = false)Integer pageSize){

        if (page==null){
            page=0;
        }
        if (pageSize==null){
            pageSize=20;
        }
        return photoService.getAlbumPhoto(album,PageRequest.of(page,pageSize));
    }


    private ResponseEntity<InputStreamResource> getPhotoFile(@NotNull String fileName,String name) throws IOException {

        GridFsResource gridFsResource = photoService.getPhotoResource(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "cache, store, must-revalidate");
        headers.add("Content-Disposition", "attachment; fileName="+  name +";filename*=utf-8''"+ URLEncoder.encode(name,"UTF-8"));
        headers.add("Pragma", "cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(gridFsResource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(gridFsResource.getInputStream()));
    }
}
