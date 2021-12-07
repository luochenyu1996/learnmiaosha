package com.chenyu.learnmiaosha.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * 时间 反序列化
 *
 * @author chen yu
 * @create 2021-12-06 15:22
 */
public class JodaDateTimeJsonDeserializer extends JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String dateString =jsonParser.readValueAs(String.class);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        return DateTime.parse(dateString,formatter);
    }
}
