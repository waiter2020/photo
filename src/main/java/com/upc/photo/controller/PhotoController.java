package com.upc.photo.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.upc.photo.model.Photo;

import com.upc.photo.model.PhotoType;
import com.upc.photo.service.PhotoService;
import org.bson.types.ObjectId;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @Author: waiter
 * @Date: 2019/4/1 20:57
 * @Version 1.0
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
    public Object upload(@RequestParam("file") MultipartFile  file) throws IOException {
        UUID uuid = UUID.randomUUID();
        Photo photo = new Photo();
        photo.setName(file.getOriginalFilename());
        photo.setFileName(uuid+"-"+file.getOriginalFilename());
        photo.setType(PhotoType.SCENERY);
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), uuid+"-"+file.getOriginalFilename());
        if (fileId==null){
            throw new IOException();
        }
        Photo save = photoService.save(photo);
        return save;
    }

    @GetMapping("/get/{photoId}")
    public ResponseEntity<InputStreamResource> get(@PathVariable("photoId") Photo photo,Authentication authentication) throws IOException {
        Assert.isTrue(((UserDetails)authentication.getPrincipal()).getUsername().equals(photo.getAuthor()),"无权访问");
        GridFSFile myFile = gridFsTemplate.findOne(query(whereFilename().is(photo.getFileName())));
        if (myFile==null){
            return null;
//            myFile=gridFsTemplate.findOne(query());
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
