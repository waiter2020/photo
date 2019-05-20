package com.upc.photo.service.impl;

import com.upc.photo.dao.ShareDao;
import com.upc.photo.model.Share;
import com.upc.photo.service.ShareService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/5/20 11:33
 * @Version 1.0
 */
@Service
public class ShareServiceImpl implements ShareService {
    private final ShareDao shareDao;

    public ShareServiceImpl(ShareDao shareDao) {
        this.shareDao = shareDao;
    }


    @Override
    public Share get(String id) {
        return shareDao.findById(id).get();
    }

    @Override
    public Share add(Share share) {
        return shareDao.save(share);
    }

    @Override
    public void delete(String id) {
        shareDao.deleteById(id);
    }

    @Override
    public List<Share> finAll(String author) {
        return shareDao.findAllByAuthorOrderByExpirationDesc(author);
    }
}
