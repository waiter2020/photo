package com.upc.photo.utils;

import com.alibaba.fastjson.JSON;
import com.upc.photo.dao.FaceDao;
import com.upc.photo.model.Face;
import com.upc.photo.model.FaceGroup;
import com.upc.photo.service.FaceGroupService;
import com.upc.photo.service.PhotoService;
import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Author: waiter
 * @Date: 2019/6/10 17:56
 * @Version 1.0
 */
public class GetFaceGroup {
    private final FaceGroupService faceGroupService;
    private final FaceDao faceDao;
    private  GetFaces getFaces;
    private final ApplicationContext context;
    public GetFaceGroup(FaceGroupService faceGroupService, FaceDao faceDao, ApplicationContext context) {
        this.faceGroupService = faceGroupService;
        this.faceDao = faceDao;
        this.context = context;

    }

    public List<FaceGroup> getGetFaceGroup(List<Face> faces) {
        if (faces.size()<2){
            return null;
        }

        List<Face> nullFace = new LinkedList<>();
        for (int i =0 ; i < faces.size();i++){
            Face f = faces.get(i);
            if (f.getMatrix()==null){
                nullFace.add(faces.remove(i));
                i--;
            }
        }
        if (getFaces==null){
            getFaces = new GetFaces(context.getBean(PhotoService.class),faceDao);
        }
        if (nullFace.size()>0) {
            List<Face> matrix = getFaces.getMatrix(nullFace);
            faces.addAll(matrix);
        }
        String [] res= new String[faces.size()];
        for(int i=0;i<faces.size();i++){
            res[i]=Arrays.toString(faces.get(i).getMatrix());
        }
        String s = Arrays.toString(res);

        String url = "http://101.132.132.225:8000/face_api/faceCluster";
        String resultString=null;
        FaceGroupResult result = null;
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Connection", "Close");
            StringEntity stringEntity = new StringEntity(String.format("{ \"instances\" : %s }", s), Charset.forName("utf-8"));


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

        //System.out.println(resultString);
        result = JSON.parseObject(resultString, FaceGroupResult.class);
        return result==null? null :getResult(faces,result);

    }

    private List<FaceGroup> getResult(List<Face> faces, FaceGroupResult result) {
        int[] cluster = result.getCluster();
        Face face = faces.get(0);
        FaceGroup faceGroup = new FaceGroup();
        faceGroup.setAuthor(face.getAuthor());
        faceGroup.setFaces(new HashMap<>(2));
        faceGroup.setCreate(new Date());
        faceGroup.setModify(new Date());
        String s = JSON.toJSONString(faceGroup);
        LinkedList<FaceGroup> faceGroups = new LinkedList<>();
        boolean f = true;
        int num = 0;
        while (f){
            f = false;
            faceGroup = JSON.parseObject(s, FaceGroup.class);
            faceGroup.setId(BigInteger.probablePrime(80,new Random()));
            for (int i = 0; i <cluster.length ; i++) {
                face = faces.remove(i);
                if (cluster[i]==-1){
                    face.setGroupId(null);
                }
                if (cluster[i]==num){
                    f=true;
                    Map<String,String> faces1 = faceGroup.getFaces();
                    if (face.getGroupId()!=null) {
                        try {
                            FaceGroup byId = faceGroupService.findById(new BigInteger(face.getGroupId()));
                            faceGroup.setName(byId.getName());
                        }catch (Exception ignored){

                        }

                    }
                    face.setGroupId(faceGroup.getId().toString());
                    faces1.put(face.getId().toString(),face.getPhotoId().toString());
                    faceGroup.setFaces(faces1);
                    faceGroup.setFace(face.getId().toString());
                }
                faces.add(i,face);
            }
            if (f){
                faceGroups.add(faceGroup);
            }
            num++;
        }
        faceGroupService.deleteAll(face.getAuthor());
        faceDao.saveAll(faces);
        return faceGroupService.saveAll(faceGroups);
    }


}
@Data
class FaceGroupResult{
    private int[] cluster;
}