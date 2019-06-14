package com.upc.photo.utils;

import com.alibaba.fastjson.JSON;

import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;


import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/4/12 17:53
 * @Version 1.0
 */
@Component
public class GetPhotoType {

    private static final String[] types = {"AEROPLANE", "BICYCLE", "BIRD", "BOAT",
            "BOTTLE", "BUS", "CAR", "CAT", "CHAIR", "COW", "DININGTABLE", "DOG",
            "HORSE", "MOTORBIKE", "PERSON", "POTTEDPLANT", "SHEEP", "SOFA", "TRAIN", "TVMONITOR"};


    public  List<String> getPhotoType(byte[] bytes) {
        String url = "http://101.132.132.225:8501/v1/models/classifier:predict";
        String resultString = RequestUtils.get(url,bytes,"{ \"instances\" : [{ \"b64\": \"%s\" }]}");
        Result result = JSON.parseObject(resultString, Result.class);
        return result==null? Collections.singletonList("DEFAULT") :getResult(result);
    }


    private  List<String> getResult(Result result){
        double[][] predictions = result.getPredictions();
        double[] doubles = predictions[0];
        ArrayList<String> type = new ArrayList<>();
        int i=0;

        for (Double d:doubles){
            if (d>0.75){
              type.add(types[i]);
            }
            i++;
        }
        return type;

    }


    public  String[] getTypes() {
        return types;
    }
}

@Data
class Result{
    private double[][] predictions;
}