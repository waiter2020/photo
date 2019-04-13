package com.upc.photo.controller;

import com.upc.photo.model.Album;
import com.upc.photo.service.AlbumService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author: waiter
 * @Date: 2019/4/12 22:21
 * @Version 1.0
 * 相册
 */
@RestController
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    /**
     * 创建新相册
     * @param album
     * @return
     */
    @PostMapping("/create")
    public Album createAlbum(Album album){
        return albumService.createAlbum(album);
    }


    /**
     * 修改相册
     * @param album
     * @return
     */
    @PreAuthorize("#album.author==authentication.principal.username or hasAuthority('ADMIN')")
    @PostMapping("/change")
    public Album changeAlbum(@RequestParam("id") Album album,
                             @RequestParam("name") String name,
                             @RequestParam("description") String description){
        album.setDescription(description);
        album.setName(name);
        return albumService.saveAlbum(album);
    }


    /**
     * 删除相册
     * @param album
     * @return
     */
    @PreAuthorize("#album.author==authentication.principal.username or hasAuthority('ADMIN')")
    @PostMapping("/delete")
    public Boolean deleteAlbum(@RequestParam("id") Album album){
        albumService.deleteAlbum(album);
        return true;
    }



    /**
     * 获取当前用户全部相册
     * @return
     */
    @GetMapping("/get_all_album")
    public ArrayList<Album> getAll(Authentication authentication){
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return albumService.getAllAlbum(username);
    }
}
