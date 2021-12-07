package com.chenyu.learnmiaosha.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * 时间 序列化
 *
 * @author chen yu
 * @create 2021-12-06 15:23
 */
public class JodaDateTimeJsonSerializer  extends JsonSerializer<DateTime> {


    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
    }
}
