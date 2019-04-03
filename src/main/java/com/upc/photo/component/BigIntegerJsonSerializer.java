package com.upc.photo.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by  waiter on 18-11-24  下午1:40.
 *
 * @author waiter
 */
public class BigIntegerJsonSerializer extends JsonSerializer<BigInteger> {
    @Override
    public void serialize(BigInteger value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {

        jgen.writeString(value.toString());
    }
}

