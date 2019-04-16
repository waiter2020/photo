package com.upc.photo.component;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.upc.photo.model.Role;
import com.upc.photo.model.User;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by  waiter on 18-11-24  下午1:40.
 *
 * @author waiter
 */
public class UserJsonSerializer extends StdSerializer<User> {

    public UserJsonSerializer(){
        super(User.class);
    }



    protected UserJsonSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User value, JsonGenerator gen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        serialize(value,gen);
    }

    private void serialize(User value, JsonGenerator gen) throws IOException {
        ArrayList<Role> roles = new ArrayList<>();
        roles.addAll((Collection<? extends Role>) value.getAuthorities());
        value.setAuthorities(roles);

        gen.writeString(JSON.toJSONString(value));
    }

    @Override
    public void serializeWithType(User value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(value,gen);
    }
}

