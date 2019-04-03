package com.upc.photo.service.impl;

import com.upc.photo.dao.PhotoDao;
import com.upc.photo.model.Photo;
import com.upc.photo.service.PhotoService;
import org.springframework.stereotype.Service;

/**
 * @Author: waiter
 * @Date: 2019/4/1 21:32
 * @Version 1.0
 */
@Service
public class PhotoServiceImpl implements PhotoService {
    private final PhotoDao photoDao;

    public PhotoServiceImpl(PhotoDao photoDao) {
        this.photoDao = photoDao;
    }


    @Override
    public Photo save(Photo photo) {
        return photoDao.save(photo);
    }
}
