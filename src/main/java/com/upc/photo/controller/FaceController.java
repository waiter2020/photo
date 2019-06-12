package com.upc.photo.controller;

import com.upc.photo.model.Face;
import com.upc.photo.model.FaceGroup;
import com.upc.photo.service.FaceGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/5/29 11:27
 * @Version 1.0
 */
@RestController
@RequestMapping("/face")
@Api
public class FaceController {
    private final FaceGroupService faceGroupService;

    public FaceController(FaceGroupService faceGroupService) {
        this.faceGroupService = faceGroupService;
    }

    @ApiOperation("获取用户相册中的人脸")
    @GetMapping("/get_all")
    public List<FaceGroup> getAllFace(Authentication authentication){
        ArrayList<FaceGroup> all = faceGroupService.findAll(((UserDetails) authentication.getPrincipal()).getUsername());
        ArrayList<FaceGroup> faceGroups = new ArrayList<>();
        all.forEach(faceGroup -> {
            if (faceGroup.getFaces().size()<3){
                faceGroups.add(faceGroup);
            }
        });
        all.removeAll(faceGroups);
        return all;
    }

    @ApiOperation("根据groupID取分组")
    @GetMapping("/get_group/{id}")
    public FaceGroup getById(@PathVariable("id") FaceGroup faceGroup){
        return faceGroup;
    }


    @ApiOperation("获取一张人脸图像")
    @GetMapping("/get_face/{id}")
    public ResponseEntity<InputStreamResource> getFace(@PathVariable("id")Face face) throws IOException {

        Assert.notNull(face,"id错误");

        byte[] bytes = face.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "cache, store, must-revalidate");
        headers.add("Content-Disposition", "attachment; fileName="+  face.getName() +";filename*=utf-8''"+ URLEncoder.encode(face.getName(),"UTF-8"));
        headers.add("Pragma", "cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(bytes.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(stream));
    }

}
