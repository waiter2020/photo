package com.upc.photo.service.impl;

import com.upc.photo.dao.FaceGroupDao;
import com.upc.photo.model.Face;
import com.upc.photo.model.FaceGroup;
import com.upc.photo.model.User;
import com.upc.photo.service.FaceGroupService;
import com.upc.photo.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    public FaceGroupServiceImpl(FaceGroupDao faceGroupDao, UserService userService) {
        this.faceGroupDao = faceGroupDao;
        this.userService = userService;
    }


    @Override
    public ArrayList<FaceGroup> findAll(String username) {
        ArrayList<FaceGroup> allByAuthor = faceGroupDao.findAllByAuthor(username);
        ArrayList<FaceGroup> faceGroups = new ArrayList<>();
        allByAuthor.forEach(e->{
            if (e.getFace()==null){
                faceGroups.add(e);
            }
        });
        allByAuthor.removeAll(faceGroups);
        return allByAuthor;
    }

    @Override
    public void analyze(List<Face> faces) {
        ArrayList<FaceGroup> all = findAll(faces.get(0).getAuthor());
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
                ArrayList<Face> objects = new ArrayList<>();
                objects.add(f);
                faceGroup.setFaces(objects);
                faceGroupDao.save(faceGroup);
                return;
            }

            FaceGroup faceGroup = faceGroup0[0];
            List<Face> faces1 = faceGroup.getFaces();
            faces1.add(f);
            faceGroup.setFaces(faces1);
            faceGroupDao.save(faceGroup);
        });
    }

    @Scheduled(fixedRate=1000*60*5)
    public void tasks() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        List<User> all = userService.findAll();
        all.forEach(u->{
            ArrayList<FaceGroup> allByAuthor = faceGroupDao.findAllByAuthor(u.getUsername());
            ArrayList<FaceGroup> deleteGroups = new ArrayList<>();
            for (int i = 0; i <allByAuthor.size()-1 ; i++) {
                FaceGroup faceGroup = allByAuthor.get(i);
                Array2DRowRealMatrix array2DRowRealMatrix = new Array2DRowRealMatrix(faceGroup.getFace().getMatrix());
                for (int j = i+1; j <allByAuthor.size(); j++) {
                    FaceGroup faceGroup1 = allByAuthor.get(j);
                    Array2DRowRealMatrix array2DRowRealMatrix1 = new Array2DRowRealMatrix(faceGroup1.getFace().getMatrix());
                    double dist = getDist(array2DRowRealMatrix, array2DRowRealMatrix1);
                    if (dist<1){
                        deleteGroups.add(faceGroup1);
                        allByAuthor.remove(faceGroup1);
                        j--;
                        List<Face> faces = faceGroup.getFaces();
                        faces.addAll(faceGroup1.getFaces());
                        faceGroup.setFaces(faces);
                    }
                }
            }
            faceGroupDao.saveAll(allByAuthor);
            faceGroupDao.deleteAll(deleteGroups);
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
