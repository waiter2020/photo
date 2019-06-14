package com.upc.photo.service;

import com.drew.imaging.ImageProcessingException;
import com.upc.photo.model.Address;
import com.upc.photo.model.Face;
import com.upc.photo.model.Photo;
import com.upc.photo.service.impl.PhotoServiceImpl;
import com.upc.photo.utils.GetAddressByBaidu;
import com.upc.photo.utils.GetFaces;
import com.upc.photo.utils.GetPhotoType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PhotoServiceTest {

    @Autowired
    private PhotoService photoService;


    @Test
    public void test() throws IOException {
        GridFsResource photoResource = photoService.getPhotoResource("ea5a74df-9d56-468c-acce-82c697ed852e-bg.jpg");
        File file = new File("./ea5a74df-9d56-468c-acce-82c697ed852e-bg.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(inputStreamConvertToByteArray(photoResource.getInputStream()));
        fileOutputStream.close();
        photoService.delete(photoService.findById(new BigInteger("28692422848912768081343273545")));
    }

    @Autowired
    GetPhotoType getPhotoType;


    @Test
    public void test2() throws IOException, ImageProcessingException {
        File file = new File("E:\\Py\\untitled\\8.jpg");
        byte[] bytes = inputStreamConvertToByteArray(new FileInputStream(file));
//        List<Face> face = getFaces.getFace(new BigInteger("131216465465"),"123456", bytes);
//        System.out.println(face);
        Photo photo = new Photo();
        photo.setFileName("8.jpg");
        photo.setThumbnailName("-" + "thumbnail" + "-"+"8.jpg");
        photo.setAuthor("123456");
        photoService.saveFile(bytes,photo,null);
        while (true){

        }
    }

    @Test
    public void test3(){
//        ArrayList<Photo> all = photoService.findAll("1234567");
//        CopyOnWriteArrayList<Photo> photos = photoService.getPhotos();
//        photos.add(all.get(0));
//        photos.add(all.get(1));
//        photos.add(all.get(2));
//        photos.add(all.get(3));
//        photoService.sync();

    }

    /**
     * 把一个文件转化为byte字节数组。
     *
     * @return
     */
    private static byte[] inputStreamConvertToByteArray(InputStream fis) {
        byte[] data = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();

            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}