package com.upc.photo.component;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.upc.photo.model.User;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @Author: waiter
 * @Date: 2019/4/15 21:11
 * @Version 1.0
 */

public class UserJsonDeSerializer extends JsonDeserializer<User> {

    @Override
    public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return JSON.parseObject(p.getText().trim(),User.class);
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return JSON.parseObject(p.getText().trim(),User.class);
    }
}
