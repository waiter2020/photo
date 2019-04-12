package com.upc.photo.service.impl;

import com.upc.photo.dao.AlbumDao;
import com.upc.photo.service.AlbumService;

/**
 * @Author: waiter
 * @Date: 2019/4/12 17:10
 * @Version 1.0
 */

public class AlbumServiceImpl implements AlbumService {
    private final AlbumDao albumDao;

    public AlbumServiceImpl(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }
}
