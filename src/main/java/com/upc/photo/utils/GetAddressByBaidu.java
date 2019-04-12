package com.upc.photo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.photo.model.Address;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * @Author: waiter
 * @Date: 2019/4/12 17:53
 * @Version 1.0
 */

public class GetAddressByBaidu {

    public static Address getAddress(String latitude, String longitude) {
        Address address=null;
        String key = "ufySmrMjKzItyY0cQ7CUFKGouuryXQLB";
        String url = String.format("http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=%s,%s&output=json&pois=1&latest_admin=1&ak=%s", latitude, longitude, key);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader insr = null;
        BufferedReader br = null;
        try {
            httpsConn = (URLConnection) myURL.openConnection();
            if (httpsConn != null) {
                insr = new InputStreamReader(httpsConn.getInputStream(), StandardCharsets.UTF_8);
                br = new BufferedReader(insr);
                StringBuilder string = new StringBuilder();
                String data = null;
                while ((data = br.readLine()) != null) {
                    string.append(data);
                }
                ObjectMapper mapper = new ObjectMapper();
                String substring = string.substring(string.indexOf("(")+1, string.indexOf(")"));
                JsonNode rootNode = mapper.readTree(substring);
                JsonNode result = rootNode.get("result");
                JsonNode addressComponent = result.get("addressComponent");
                address = mapper.readValue(addressComponent.toString(), Address.class);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }



}

