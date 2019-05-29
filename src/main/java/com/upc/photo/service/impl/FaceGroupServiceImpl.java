package com.upc.photo.service.impl;

import com.upc.photo.dao.FaceGroupDao;
import com.upc.photo.model.Face;
import com.upc.photo.model.FaceGroup;
import com.upc.photo.service.FaceGroupService;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.stereotype.Service;

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

    public FaceGroupServiceImpl(FaceGroupDao faceGroupDao) {
        this.faceGroupDao = faceGroupDao;
    }


    @Override
    public ArrayList<FaceGroup> findAll(String username) {
        return faceGroupDao.findAllByAuthor(username);
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
                System.out.println(dist);
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
