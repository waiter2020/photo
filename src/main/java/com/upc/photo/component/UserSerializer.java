package com.upc.photo.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.upc.photo.model.Role;
import com.upc.photo.model.User;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by  waiter on 18-11-24  下午1:40.
 *
 * @author waiter
 */
public class UserSerializer extends StdSerializer<User> {

    public UserSerializer(){
        super(User.class);
    }



    protected UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {

        jgen.writeString(value.toString());
    }

    @Override
    public void serializeWithType(User value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeString(value.toString());
    }
}

