package com.upc.photo.service.impl;

import com.upc.photo.dao.FaceDao;
import com.upc.photo.dao.FaceGroupDao;
import com.upc.photo.model.Face;
import com.upc.photo.model.FaceGroup;
import com.upc.photo.model.User;
import com.upc.photo.service.FaceGroupService;
import com.upc.photo.service.UserService;
import com.upc.photo.utils.GetFaceGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/5/28 20:25
 * @Version 1.0
 */
@Service
public class FaceGroupServiceImpl implements FaceGroupService {
    private final FaceGroupDao faceGroupDao;
    private final Log logger = LogFactory.getLog(FaceGroupServiceImpl.class);
    private final UserService userService;
    private final FaceDao faceDao;
    private final GetFaceGroup getFaceGroup;

    public FaceGroupServiceImpl(FaceGroupDao faceGroupDao, UserService userService, FaceDao faceDao) {
        this.faceGroupDao = faceGroupDao;
        this.userService = userService;
        this.faceDao = faceDao;
        this.getFaceGroup = new GetFaceGroup(this,faceDao);
    }


    @Override
    public ArrayList<FaceGroup> findAll(String username) {
        return faceGroupDao.findAllByAuthor(username);
    }

    @Override
    public void deleteAll(String userName) {
        faceGroupDao.deleteAllByAuthor(userName);
    }



    @Override
    public FaceGroup findById(BigInteger id) {
        return faceGroupDao.findById(id).get();
    }

    @Override
    public List<FaceGroup> saveAll(Iterable<? extends FaceGroup> faceGroups) {
        return faceGroupDao.saveAll((Iterable<FaceGroup>) faceGroups);
    }

    @Scheduled(fixedRate=1000*60*5)
    public void tasks() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        List<User> all = userService.findAll();
        all.forEach(u->{
            ArrayList<Face> allByAuthor = faceDao.findAllByAuthor(u.getUsername());
            getFaceGroup.getGetFaceGroup(allByAuthor);
        });

    }



}
