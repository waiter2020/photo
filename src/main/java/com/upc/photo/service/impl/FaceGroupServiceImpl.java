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
    public void analyze(List<Face> faces) {
        ArrayList<FaceGroup> all = findAll(faces.get(0).getAuthor());
        ArrayList<FaceGroup> faceGroups = new ArrayList<>();
        all.forEach(e->{
            if (e.getFace()==null){
                faceGroups.add(e);
            }
        });
        all.removeAll(faceGroups);
        faces.forEach(f->{
            RealMatrix matrix = new Array2DRowRealMatrix(f.getMatrix());
            final double[] minDist = {2};
            final FaceGroup[] faceGroup0 = {null};

            all.forEach(faceGroup -> {
                RealMatrix matrix2 = new Array2DRowRealMatrix(faceGroup.getFace().getMatrix());
                double dist = getDist(matrix, matrix2);
                logger.warn(dist);
                if (dist< minDist[0]){
                    minDist[0] = dist;
                    faceGroup0[0] =faceGroup;
                }
            });

            if (faceGroup0[0]==null||minDist[0]>1){
                FaceGroup faceGroup = new FaceGroup();
                faceGroup.setAuthor(f.getAuthor());
                faceGroup.setFace(f);
                ArrayList<String> objects = new ArrayList<>();
                objects.add(f.getId().toString());
                faceGroup.setFaces(objects);
                faceGroupDao.save(faceGroup);
                return;
            }

            FaceGroup faceGroup = faceGroup0[0];
            List<String> faces1 = faceGroup.getFaces();
            faces1.add(f.getId().toString());
            faceGroup.setFaces(faces1);
            faceGroupDao.save(faceGroup);
        });
    }

    @Override
    public FaceGroup findById(BigInteger id) {
        return faceGroupDao.findById(id).get();
    }

    @Override
    public List<FaceGroup> saveAll(Iterable<? extends FaceGroup> faceGroups) {
        return faceGroupDao.saveAll((Iterable<FaceGroup>) faceGroups);
    }

   // @Scheduled(fixedRate=1000*60*5)
    public void tasks() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        List<User> all = userService.findAll();
        all.forEach(u->{
            ArrayList<Face> allByAuthor = faceDao.findAllByAuthor(u.getUsername());
            getFaceGroup.getGetFaceGroup(allByAuthor);
        });

    }


    private double getDist(RealMatrix matrix1,RealMatrix matrix2){
        RealMatrix subtract = matrix1.subtract(matrix2);
        double[][] data = subtract.getData();
        double sum = 0;
        for (double[] d : data) {
            for (double dd : d) {
                sum+=Math.pow(dd,2);
            }
        }
        return Math.sqrt(sum);
    }
}
