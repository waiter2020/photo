package com.upc.photo.service;

import com.upc.photo.model.Share;

import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/5/20 11:29
 * @Version 1.0
 */

public interface ShareService {
    Share get(String id);
    Share add(Share share);
    void delete(String id);
    List<Share> finAll(String author );
}
