package com.upc.photo.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @Author: waiter
 * @Date: 2019/5/27 10:27
 * @Version 1.0
 */

public class GetFaces {
    public static String getFace(byte[] bytes) throws IOException {
        String url = "http://101.132.132.225:8000/face_api/faceAlign";
        String resultString = RequestUtils.get(url, bytes,"{ \"instances\" : \"%s\" }");
        FacesResult faces = JSON.parseObject(resultString, FacesResult.class);
        Base64.Decoder decoder = Base64.getDecoder();
        int i=0;
        for (String s:faces.getFaces()){
            byte[] decode = decoder.decode(s);
            File file = new File(String.format("face-%d.jpg", i));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(decode);
            fileOutputStream.close();

            i++;
        }
        return null;
    }
}
@Data
class FacesResult{
    private String [] faces;

}