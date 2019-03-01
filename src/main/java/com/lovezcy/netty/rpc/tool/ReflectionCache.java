package com.lovezcy.netty.rpc.tool;

import com.lovezcy.netty.rpc.tool.struct.LRUMap;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingzhaolei
 * @date 2019/2/27 16:24
 **/
public class ReflectionCache {
    private static final Map<String,Class<?>> PRIMITIVE_CLASS = new HashMap<>();
    private static final Map<String,Class<?>> CLASS_CACHE = new LRUMap<>(128);
    private static final Map<String,Method> METHOD_CACHE = new LRUMap<>(1024);

    static {
        PRIMITIVE_CLASS.put("boolean", boolean.class);
        PRIMITIVE_CLASS.put("byte", byte.class);
        PRIMITIVE_CLASS.put("short", short.class);
        PRIMITIVE_CLASS.put("int", int.class);
        PRIMITIVE_CLASS.put("long", long.class);
        PRIMITIVE_CLASS.put("float", float.class);
        PRIMITIVE_CLASS.put("double", double.class);
        PRIMITIVE_CLASS.put("void", void.class);

        CLASS_CACHE.putAll(PRIMITIVE_CLASS);
    }


    public static Class<?> getClass (String className) throws ClassNotFoundException{
        Class<?> clazz = CLASS_CACHE.get(className);

        if (clazz == null){
            synchronized (CLASS_CACHE){
                clazz = CLASS_CACHE.get(className);
                if (clazz == null){
                    clazz = Class.forName(className);
                    CLASS_CACHE.put(className,clazz);
                }
                return clazz;
            }
        }else {
            return clazz;
        }
    }

    public static Method getMethod(String  className, String methodName, Class<?>[] parameterTypes) throws ClassNotFoundException,NoSuchMethodException{

        List<String> parameterList = new LinkedList<>();

        for (Class<?> c:parameterTypes){
            parameterList.add(c.getName());
        }
        String parameters = parameterList.stream().collect(Collectors.joining(","));
        String key = className+"-"+methodName+"-"+parameters;

        Method method = METHOD_CACHE.get(key);
        if (method!=null){
            return method;
        }
        synchronized (METHOD_CACHE){
            method = METHOD_CACHE.get(key);
            if (method != null){
                return method;
            }
            Class<?> clazz = getClass(className);
            method = clazz.getMethod(methodName,parameterTypes);
            METHOD_CACHE.put(key,method);
            return method;
        }

    }

}