package com.upc.photo.service.impl;

import com.upc.photo.dao.FaceGroupDao;
import com.upc.photo.model.Face;
import com.upc.photo.model.FaceGroup;
import com.upc.photo.service.FaceGroupService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;
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
    private final Log logger = LogFactory.getLog(FaceGroupServiceImpl.class);

    public FaceGroupServiceImpl(FaceGroupDao faceGroupDao) {
        this.faceGroupDao = faceGroupDao;
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

            ArrayList<FaceGroup> faceGroups = new ArrayList<>();
            all.forEach(faceGroup -> {
                RealMatrix matrix2 = new Array2DRowRealMatrix(faceGroup.getFace().getMatrix());
                double dist = getDist(matrix, matrix2);
                logger.warn(dist);
                if (dist< 1){
                    faceGroups.add(faceGroup);
                }
            });

            if (faceGroups.size()<1){
                FaceGroup faceGroup = new FaceGroup();
                faceGroup.setAuthor(f.getAuthor());
                faceGroup.setFace(f);
                ArrayList<Face> objects = new ArrayList<>();
                objects.add(f);
                faceGroup.setFaces(objects);
                faceGroupDao.save(faceGroup);
                return;
            }

            faceGroups.forEach(faceGroup -> {
                List<Face> faces1 = faceGroup.getFaces();
                faces1.add(f);
                faceGroup.setFaces(faces1);
                faceGroupDao.save(faceGroup);
            });


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
