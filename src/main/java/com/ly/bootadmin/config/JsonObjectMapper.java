package com.ly.bootadmin.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 重写jackson对null value的设置,null的时候变为"",
 *
 * @author linyun
 * @date 2018/11/17 13:59
 */
@Slf4j
public class JsonObjectMapper extends ObjectMapper {
    public JsonObjectMapper(){
        super();
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(StringUtils.EMPTY);
            }
        });
    }
}
