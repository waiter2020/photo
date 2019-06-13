package com.upc.photo.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @Author: waiter
 * @Date: 2019/6/13 9:15
 * @Version 1.0
 */

public class ByteUtils {
    public static byte[] inputStreamConvertToByteArray(InputStream fis) {
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
