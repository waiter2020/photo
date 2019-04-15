package com.upc.photo.component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @Author: waiter
 * @Date: 2019/4/15 21:11
 * @Version 1.0
 */

public class BigIntegerJsonDeSerializer extends JsonDeserializer<BigInteger> {

    @Override
    public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new BigInteger(p.getText().trim());
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return new BigInteger(p.getText().trim());
    }
}
