package com.upc.photo.service;

import com.upc.photo.model.Face;
import com.upc.photo.model.FaceGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/5/28 20:24
 * @Version 1.0
 */

public interface FaceGroupService {
    ArrayList<FaceGroup> findAll(String username);

    void analyze(List<Face> faces);

}