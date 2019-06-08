package com.upc.photo.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.upc.photo.dao.FaceDao;
import com.upc.photo.dao.PhotoDao;
import com.upc.photo.model.Album;
import com.upc.photo.model.Face;
import com.upc.photo.model.Photo;

import com.upc.photo.model.RestPage;
import com.upc.photo.service.PhotoService;
import com.upc.photo.utils.GetAddressByBaidu;
import com.upc.photo.utils.GetFaces;
import com.upc.photo.utils.GetPhotoType;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

/**
 * @Author: waiter
 * @Date: 2019/4/1 21:32
 * @Version 1.0
 */
@Service
public class PhotoServiceImpl implements PhotoService {
    private final PhotoDao photoDao;
    private final GridFsTemplate gridFsTemplate;
    private final GetPhotoType getPhotoType;
    private final GetAddressByBaidu addressByBaidu;
    private final GetFaces getFaces;
    private final FaceDao faceDao;
    private final FaceGroupServiceImpl faceGroupService;

    public PhotoServiceImpl(PhotoDao photoDao, GridFsTemplate gridFsTemplate, GetPhotoType getPhotoType, GetAddressByBaidu addressByBaidu, GetFaces getFaces, FaceDao faceDao, FaceGroupServiceImpl faceGroupService) {
        this.photoDao = photoDao;
        this.gridFsTemplate = gridFsTemplate;
        this.getPhotoType = getPhotoType;
        this.addressByBaidu = addressByBaidu;
        this.getFaces = getFaces;
        this.faceDao = faceDao;
        this.faceGroupService = faceGroupService;
    }


    @Override
    public Photo findOneByCity(String userName,String cityName) {
        return photoDao.findTopByAuthorAndAddress_City(userName,cityName);
    }

    @Override
    public Photo findOneByDistrict(String username, String district) {
        return photoDao.findTopByAuthorAndAddress_District(username,district);
    }

    @Override
    public Photo findOneByProvince(String username, String province) {
        return photoDao.findTopByAuthorAndAddress_Province(username,province);
    }

    @Override
    public Photo findOneByType(String userName, String type) {

        return photoDao.findTopByAuthorAndTypeContaining(userName,type);
    }

    @Override
    public Long countByCity(String userName, String cityName) {
        return photoDao.countAllByAuthorAndAddress_City(userName,cityName);
    }

    @Override
    public Page<Photo> getByDistrict(String username, String district, PageRequest of) {
        return photoDao.findAllByAuthorAndAddress_DistrictOrderByUploadDesc(username,district,of);
    }

    @Override
    public Page<Photo> getByProvince(String username, String province, PageRequest of) {
        return photoDao.findAllByAuthorAndAddress_ProvinceOrderByUploadDesc(username,province,of);
    }

    @Override
    public Map<String,Long> getCityList(String userName) {
        ArrayList<Photo> all = photoDao.findAllByAuthorAndAddressNotNull(userName);
        HashSet<String> citys = new HashSet<>();
        all.forEach(e-> citys.add(e.getAddress().getCity()));
        Iterator<String> iterator = citys.iterator();
        HashMap<String,Long> map = new HashMap<>();
        while (iterator.hasNext()){
            String next = iterator.next();
            map.put(next,countByCity(userName,next));
        }
        return map;
    }

    @Override
    public Map<String, Long> getDistrictList(String username) {
        ArrayList<Photo> all = photoDao.findAllByAuthorAndAddressNotNull(username);
        HashSet<String> provinces = new HashSet<>();
        all.forEach(e-> provinces.add(e.getAddress().getDistrict()));
        Iterator<String> iterator = provinces.iterator();
        HashMap<String,Long> map = new HashMap<>();
        while (iterator.hasNext()){
            String next = iterator.next();
            map.put(next,countByCity(username,next));
        }
        return map;
    }

    @Override
    public Map<String, Long> getProvinceList(String username) {
        ArrayList<Photo> all = photoDao.findAllByAuthorAndAddressNotNull(username);
        HashSet<String> provinces = new HashSet<>();
        all.forEach(e-> provinces.add(e.getAddress().getProvince()));
        Iterator<String> iterator = provinces.iterator();
        HashMap<String,Long> map = new HashMap<>();
        while (iterator.hasNext()){
            String next = iterator.next();
            map.put(next,countByCity(username,next));
        }
        return map;
    }

    @Override
    public Map<String, Long> getTypeList(String userName) {
        String[] types = getPhotoType.getTypes();
        HashMap<String, Long> stringLongHashMap = new HashMap<>();
        for (String s:types){
            Long aLong = photoDao.countAllByAuthorAndTypeContaining(userName, s);
            stringLongHashMap.put(s,aLong);
        }

        return stringLongHashMap;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplgetAlbumPhoto-'+#photo.album+'-*'", allEntries = true),
            @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplfindAll-'+#photo.author+'-*'", allEntries = true)
    })
    @Override
    public void delete(Photo photo) {
        gridFsTemplate.delete(query(whereFilename().is(photo.getFileName())));
        gridFsTemplate.delete(query(whereFilename().is(photo.getThumbnailName())));
        faceDao.deleteAll(photo.getFaces());
        photoDao.delete(photo);
    }


    @Async
    @Caching(evict = {
            @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplgetAlbumPhoto-'+#photo.album+'-*'", allEntries = true),
            @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplfindAll-'+#photo.author+'-*'", allEntries = true)
    })
    @Override
    public void saveFile(byte[] bytes,Photo photo, String md5) {
        Photo.Location location = photo.getLocationInstance();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    //标签名
                    String tagName = tag.getTagName();
                    //标签信息
                    String desc = tag.getDescription();
                    if ("GPS Latitude".equals(tagName)) {

                        location.setLatitude(pointToLatlong(desc));
                    } else if ("GPS Longitude".equals(tagName)) {

                        location.setLongitude(pointToLatlong(desc));
                    }
                }
            }

            photo.setLocation(location);

            // obtain the Exif directory
            ExifSubIFDDirectory directory
                    = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (directory!=null) {
                // query the tag's value
                Date date
                        = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                photo.setCreate(date);

            }

            ObjectId store = gridFsTemplate.store(new ByteArrayInputStream(bytes), photo.getFileName());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .size(400,200)
                    .toOutputStream(byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
            ObjectId store1 = gridFsTemplate.store(new ByteArrayInputStream(bytes), photo.getThumbnailName());
            photo = save(photo);
            photo.setAddress(addressByBaidu.getAddress(location.getLatitude(), location.getLongitude()));
            //调用py接口获取照片类别
            photo.setType(getPhotoType.getPhotoType(bytes));

        } catch (Exception e) {
            e.printStackTrace();
        }
        photo = save(photo);
        //TODO:判断是不是人，执行下一步操作
        getFace(photo,bytes);
    }

    private void getFace(Photo photo,byte[] bytes){
        String s = photo.getType().toString();
        if(!s.contains("PERSON")){
            return;
        }
        List<Face> face = getFaces.getFace(photo.getId(),photo.getAuthor(), bytes);
        if (face.isEmpty()){
            return;
        }
        List<Face> faces = faceDao.saveAll(face);

        photo.setFaces(faces);
        photoDao.save(photo);


        //TODO: 计算距离
        faceGroupService.analyze(faces);



    }

    @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplfindAll-'+'*'+#photo.author+'-*'", allEntries = true)
    @Override
    public Photo save(Photo photo) {
        return photoDao.save(photo);
    }

    /**
           * 经纬度格式  转换为  度分秒格式 ,如果需要的话可以调用该方法进行转换
           * @param point 坐标点
           * @return
           */
     private static String pointToLatlong(String point) {
                 double du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());
                 double fen = Double.parseDouble(point.substring(point.indexOf("°")+1, point.indexOf("'")).trim());
                 double miao = Double.parseDouble(point.substring(point.indexOf("'")+1, point.indexOf("\"")).trim());
                 double duStr = du + fen / 60 + miao / 60 / 60 ;
                 return Double.toString(duStr);
     }



    @Cacheable(cacheNames = "photos")
    @Override
    public ArrayList<Photo> findAll(String userName) {

        return photoDao.findAllByAuthor(userName);
    }

    @Cacheable(cacheNames = "photos")
    @Override
    public Page<Photo> findAll(String userName, Pageable pageable) {
        return new RestPage<>(photoDao.findAllByAuthorOrderByUploadDesc(userName, pageable));
    }

    @Cacheable(cacheNames = "photos")
    @Override
    public ArrayList<Photo> getAlbumPhoto(Album album) {
        return photoDao.findAllByAlbum(album);
    }

    @Cacheable(cacheNames = "photos")
    @Override
    public Page<Photo> getAlbumPhoto(Album album,Pageable pageable) {
        return new RestPage<>(photoDao.findAllByAlbumOrderByUploadDesc(album,pageable));
    }

    @Override
    public Page<Photo> getByCity(String userName, String cityName, Pageable pageable) {
        return photoDao.findAllByAuthorAndAddress_CityOrderByUploadDesc(userName,cityName,pageable);
    }

    @Override
    public Page<Photo> getByType(String userName, String type, Pageable pageable) {
        return photoDao.findAllByAuthorAndTypeContainingOrderByUploadDesc(userName,type,pageable);
    }


    @Override
    public Photo findById(BigInteger id) {


        return photoDao.findById(id).get();
    }


    @Override
    public GridFsResource getPhotoResource(String fileName) {
        GridFSFile myFile = gridFsTemplate.findOne(query(whereFilename().is(fileName)));
        if (myFile==null){
            return null;
        }
       return gridFsTemplate.getResource(myFile);

    }

    @CacheEvict(cacheNames = "photos", key = "'com.upc.photo.service.impl.PhotoServiceImplgetAlbumPhoto-'+#result.album+'-*'", allEntries = true)
    @Override
    public Photo changeToAlbum(Photo photo) {
        return photoDao.save(photo);
    }
}
