package com.upc.photo.controller;

import com.upc.photo.model.Album;
import com.upc.photo.model.Photo;
import com.upc.photo.model.Share;
import com.upc.photo.service.PhotoService;
import com.upc.photo.service.ShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @Author: waiter
 * @Date: 2019/5/20 11:41
 * @Version 1.0
 */
@RestController
@Api
@RequestMapping("/share")
public class ShareController {
    private final ShareService shareService;
    private final PhotoService photoService;
    private final PhotoController photoController;

    public ShareController(ShareService shareService, PhotoService photoService, PhotoController photoController) {
        this.shareService = shareService;
        this.photoService = photoService;
        this.photoController = photoController;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @ApiOperation(value = "添加分享",notes = "除了id和author之外其它字段都需要填上,type=0是相册分享，1是照片分享")
    @PostMapping("/add")
    public Share addShare(Share share, Authentication authentication){
        Assert.notNull(share.getExpiration(),"过期时间不能为空");
        Assert.notNull(share.getShareList(),"分享列表不为空");
        share.setId(UUID.randomUUID().toString());
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        share.setAuthor(username);
        return shareService.add(share);
    }

    @ApiOperation("获取分享信息")
    @GetMapping("/get/{id}")
    public Share getShare(@PathVariable("id") Share share){
        return share;
    }

    @ApiOperation("删除分享信息")
    @PreAuthorize("hasAnyRole('USER') and #share.author==authentication.principal.username")
    @PostMapping("/delete/{id}")
    public void deleteShare(@PathVariable("id")Share share){
        shareService.delete(share.getId());
    }

    @PreAuthorize("hasAnyRole('USER')")
    @ApiOperation("获取用户所有分享信息")
    @GetMapping("/get_all")
    public List<Share> getAllShare(Authentication authentication){
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return shareService.finAll(username);
    }


    @ApiOperation("获取分享的相册所有照片")
    @GetMapping({"/get_album_photos/{id}","/get_album_photos/{id}/{page}","/get_album_photos/{id}/{page}/{pageSize}"})
    public Page<Photo> getAlbumPhotos(@PathVariable("id") Album album,
                                      @PathVariable(value = "page",required = false)Integer page,
                                      @PathVariable(value = "pageSize",required = false)Integer pageSize){

        if (page==null){
            page=0;
        }
        if (pageSize==null){
            pageSize=20;
        }
        return photoService.getAlbumPhoto(album, PageRequest.of(page,pageSize));
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
        return photoController.getPhotoFile(photo.getFileName(),photo.getName());
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

        return photoController.getPhotoFile(photo.getThumbnailName(),photo.getName());
    }


}
