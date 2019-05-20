package com.upc.photo.dao;

import com.upc.photo.model.Share;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/5/20 11:09
 * @Version 1.0
 */

public interface ShareDao extends CrudRepository<Share,String> {
    List<Share> findAllByAuthorOrderByExpirationDesc(String author);
}
