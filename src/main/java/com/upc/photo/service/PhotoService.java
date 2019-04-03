package com.upc.photo.service;

import com.upc.photo.model.Photo;

/**
 * @Author: waiter
 * @Date: 2019/4/3 13:38
 * @Version 1.0
 */

public interface PhotoService {
    /**
     * @param photo
     * @return
     */
    Photo save(Photo photo);
}
