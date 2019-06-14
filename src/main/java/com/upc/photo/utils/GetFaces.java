package com.upc.photo.utils;

import com.alibaba.fastjson.JSON;

import com.upc.photo.dao.FaceDao;
import com.upc.photo.model.Photo;
import com.upc.photo.service.PhotoService;
import lombok.Data;
import com.upc.photo.model.Face;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Author: waiter
 * @Date: 2019/5/27 10:27
 * @Version 1.0
 */

public class GetFaces {
    private final PhotoService photoService;
    private final FaceDao faceDao;

    public GetFaces(PhotoService photoService, FaceDao faceDao) {
        this.photoService = photoService;
        this.faceDao = faceDao;
    }

    public List<Face> getFace(Photo photo, byte[] bytes) {
        String url = "http://101.132.132.225:8000/face_api/faceAlign";
        List<Face> allFace = new LinkedList<>();

        System.out.println(photo.getName() + bytes.length);
        String resultString = RequestUtils.get(url, bytes, "{ \"instances\" : \"%s\" }");
        FacesResult faces = null;

        try {
            faces = JSON.parseObject(resultString, FacesResult.class);
        } catch (Exception e) {
            System.out.println(photo.getName());
            e.printStackTrace();
        }

        Base64.Decoder decoder = Base64.getDecoder();
        ArrayList<Face> facesList = new ArrayList<>();

        int j = 0;
        for (String s : faces.getFaces()) {
            byte[] decode = decoder.decode(s);
            Face face = new Face();
            face.setAuthor(photo.getAuthor());
            face.setName(String.format("face-%d.jpg", j));
            face.setBytes(decode);
            System.out.println("人脸大小:"+decode.length);
            face.setPhotoId(photo.getId());
            face = faceDao.save(face);
            facesList.add(face);
            j++;
        }
        allFace.addAll(facesList);
        photo.setFaces(facesList);

        photoService.save(photo);
        System.out.println("人脸数"+allFace.size());

        return allFace;
    }

    public List<Face> getMatrix(List<Face> allFace) {
        String url = "http://101.132.132.225:8501/v1/models/facenet:predict";
        Base64.Encoder encoder = Base64.getEncoder();
        String[] faceByte = new String[allFace.size()];
        for (int i = 0; i < allFace.size(); i++) {
            faceByte[i] = String.format("{\"images\": { \"b64\": \"%s\" } }", new String(encoder.encode((allFace.get(i).getBytes()))));
        }
        String res = Arrays.toString(faceByte);

        String format = "{\"signature_name\": \"calculate_embeddings\", \"instances\" :  %s }";
        String finalUrl = url;

        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Connection", "Close");

            StringEntity stringEntity = new StringEntity(String.format(format, res), Charset.forName("utf-8"));


            httpPost.setEntity(stringEntity);

            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");

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

        System.out.println(resultString);
        Result result1 = JSON.parseObject(resultString, Result.class);
        double[][] predictions = result1.getPredictions();
        for (int i = 0; i < predictions.length; i++) {
            allFace.get(i).setMatrix(predictions[i]);
        }
        return allFace;
    }

}

@Data
class FacesResult {
    private String[] faces;

}