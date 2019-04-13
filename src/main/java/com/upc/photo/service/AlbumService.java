package com.upc.photo.service;

import com.upc.photo.model.Album;

import java.util.ArrayList;

/**
 * @Author: waiter
 * @Date: 2019/4/12 17:09
 * @Version 1.0
 */

public interface AlbumService {
    Album createAlbum(Album album);

    Album saveAlbum(Album album);

    void deleteAlbum(Album album);

    ArrayList<Album> getAllAlbum(String author);
}
