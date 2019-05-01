package com.upc.photo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.photo.model.Address;

import lombok.Data;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/4/12 17:53
 * @Version 1.0
 */

public class GetPhotoType {

    public static String getPhotoType(byte[] bytes) {
        String url = "http://101.132.132.225:8501/v1/models/classifier:predict";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encode = encoder.encode(bytes);

        Result result=null;
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Connection", "Close");
            String s = new String(encode);

            StringEntity stringEntity = new StringEntity(String.format("{ \"instances\" : [{ \"b64\": \"%s\" }]}",s), Charset.forName("utf-8"));


            httpPost.setEntity(stringEntity);

            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            result = JSON.parseObject(resultString, Result.class);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result==null?"DEFAULT":getResult(result);
    }


    private static String getResult(Result result){
        Double[][] predictions = result.getPredictions();
        if(predictions[0][0]<0.7&&predictions[0][1]<0.7){
            return "DEFAULT";
        }
        if (predictions[0][0]>predictions[0][1]){
            return "CAT";
        }else {
            return "DOG";
        }

    }


    @Data
    static class Result{
        Double[][] predictions;
    }



}

