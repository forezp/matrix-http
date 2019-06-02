package io.github.forezp;


import com.alibaba.fastjson.JSON;

public class SerializerExecutor {


    public static String toJson(Object object) {
        return JSON.toJSONString(object);
    }
}