package com.upc.photo.service.impl;

import com.upc.photo.dao.AlbumDao;
import com.upc.photo.model.Album;
import com.upc.photo.service.AlbumService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @Author: waiter
 * @Date: 2019/4/12 17:10
 * @Version 1.0
 */

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumDao albumDao;

    public AlbumServiceImpl(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    @CacheEvict(cacheNames = "album",key = "'com.upc.photo.service.impl.AlbumServiceImpl-getAllAlbum-'+#result.author")
    @Override
    public Album createAlbum(Album album) {
        return albumDao.save(album);
    }

    @CacheEvict(cacheNames = "album",key = "'com.upc.photo.service.impl.AlbumServiceImpl-getAllAlbum-'+#album.author")
    @Override
    public Album saveAlbum(Album album) {
        return albumDao.save(album);
    }

    @CacheEvict(cacheNames = "album",key = "'com.upc.photo.service.impl.AlbumServiceImpl-getAllAlbum-'+#album.author")
    @Override
    public void deleteAlbum(Album album) {
         albumDao.delete(album);
    }
    @Cacheable(cacheNames = "album")
    @Override
    public ArrayList<Album> getAllAlbum(String author) {
        return albumDao.findAllByAuthor(author);
    }
}
