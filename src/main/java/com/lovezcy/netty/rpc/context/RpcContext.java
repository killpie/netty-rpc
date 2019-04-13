package com.lovezcy.netty.rpc.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc上下文
 * @author dingzhaolei
 * @date 2019/4/8 15:11
 **/
public class RpcContext {
    private static Map<String,Object> props = new ConcurrentHashMap<>();

    public static void addProp(String key ,Object value){
        props.put(key,value);
    }

    public static Object getProp(String key){
        return props.get(key);
    }

    public static Map<String,Object> getProps(){
        return Collections.unmodifiableMap(props);
    }
}
