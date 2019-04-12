package com.upc.photo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: waiter
 * @Date: 2019/4/12 18:09
 * @Version 1.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Serializable {
    private String country;
    private String province;
    private String city;
    private String district;

}
//"country": "中国",
//            "country_code": 0,
//            "country_code_iso": "CHN",
//            "country_code_iso2": "CN",
//            "province": "河北省",
//            "city": "廊坊市",
//            "city_level": 2,
//            "district": "文安县",
//            "town": "",
//            "adcode": "131026",
//            "street": "",
//            "street_number": "",
//            "direction": "",
//            "distance": ""