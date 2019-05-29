package com.upc.photo.utils;

import com.alibaba.fastjson.JSON;

import lombok.Data;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.stereotype.Component;
import com.upc.photo.model.Face;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @Author: waiter
 * @Date: 2019/5/27 10:27
 * @Version 1.0
 */
@Component
public class GetFaces {
    public  List<Face> getFace(BigInteger id, String author, byte[] bytes)  {
        String url = "http://101.132.132.225:8000/face_api/faceAlign";
        String resultString = RequestUtils.get(url, bytes,"{ \"instances\" : \"%s\" }");
        FacesResult faces = JSON.parseObject(resultString, FacesResult.class);
        Base64.Decoder decoder = Base64.getDecoder();
        ArrayList<Face> facesList = new ArrayList<>();

        int i=0;
        for (String s:faces.getFaces()){
            byte[] decode = decoder.decode(s);
            Face face = new Face();
            face.setAuthor(author);
            face.setName(String.format("face-%d.jpg",i));
            face.setBytes(decode);
            face.setPhotoId(id);
            facesList.add(face);

            i++;
        }

        url = "http://101.132.132.225:8501/v1/models/facenet:predict";

        String format = "{\"signature_name\": \"calculate_embeddings\", \"instances\" : [{\"images\": { \"b64\": \"%s\" } }]}";
        String finalUrl = url;
        facesList.forEach(e->{
            String result = RequestUtils.get(finalUrl, e.getBytes(),format);
            Result result1 = JSON.parseObject(result, Result.class);
            e.setMatrix(result1.getPredictions());
        });
        return facesList;
    }
}
@Data
class FacesResult{
    private String [] faces;

}