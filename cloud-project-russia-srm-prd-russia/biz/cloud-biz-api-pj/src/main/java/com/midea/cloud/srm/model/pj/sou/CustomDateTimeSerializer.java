package com.midea.cloud.srm.model.pj.sou;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author huangbf3
 */
public class CustomDateTimeSerializer implements ObjectSerializer {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    {
        formatter.withZone(ZoneId.systemDefault());
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object == null) {
            serializer.write(null);
            return;
        }
        serializer.write(formatDate((Date)object));
    }

    private String formatDate(Date date) {
        return formatter.format(date.toInstant());
    }
}
