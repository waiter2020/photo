package com.upc.photo.controller;

import com.upc.photo.model.Photo;
import com.upc.photo.service.PhotoService;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @Author: waiter
 * @Date: 2019/4/1 20:57
 * @Version 1.0
 */

@RestController
@RequestMapping("/file")
public class FileController {

    private final GridFsTemplate gridFsTemplate;
    private final PhotoService photoService;

    public FileController(GridFsTemplate gridFsTemplate, PhotoService photoService) {
        this.gridFsTemplate = gridFsTemplate;
        this.photoService = photoService;
    }

    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile  file) throws IOException {
        UUID uuid = UUID.randomUUID();
        Photo photo = new Photo();
        photo.setName(file.getOriginalFilename());
        photo.setFileName(uuid+"-"+file.getOriginalFilename());
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), uuid+"-"+file.getOriginalFilename());
        photoService.save(photo);
        return fileId;
    }
}
