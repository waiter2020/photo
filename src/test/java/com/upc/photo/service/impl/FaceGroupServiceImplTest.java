package com.upc.photo.service.impl;

import com.upc.photo.dao.FaceDao;
import com.upc.photo.model.FaceGroup;
import com.upc.photo.service.FaceGroupService;
import com.upc.photo.utils.GetFaceGroup;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/** 
* FaceGroupServiceImpl Tester. 
* 
* @author <Authors name> 
* @since <pre>���� 10, 2019</pre> 
* @version 1.0 
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class FaceGroupServiceImplTest { 

    @Autowired
    private  FaceGroupService faceGroupService;
    @Autowired
    private  FaceDao faceDao;



    @Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: findAll(String username) 
* 
*/ 
@Test
public void testFindAll() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: analyze(List<Face> faces) 
* 
*/ 
@Test
public void testAnalyze() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: tasks() 
* 
*/ 
@Test
public void testTasks() throws Exception { 
//TODO: Test goes here...
    GetFaceGroup getFaceGroup = new GetFaceGroup(faceGroupService, faceDao);
    List<FaceGroup> getFaceGroup1 = getFaceGroup.getGetFaceGroup(faceDao.findAllByAuthor("1234567"));

} 


/** 
* 
* Method: getDist(RealMatrix matrix1, RealMatrix matrix2) 
* 
*/ 
@Test
public void testGetDist() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = FaceGroupServiceImpl.getClass().getMethod("getDist", RealMatrix.class, RealMatrix.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
