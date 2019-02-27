package com.lovezcy.netty.rpc.tool;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * protostuff 序列化工具类
 * @author dingzhaolei
 * @date 2019/2/27 10:47
 **/
public class Tool {

    public static <T> byte[] serialize(T t){
        Class<T> clzss = (Class<T>)t.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Schema<T> schema = RuntimeSchema.getSchema(clzss);

        try {
            byte[] bytes = ProtobufIOUtil.toByteArray(t,schema,buffer);
            return bytes;
        }catch (Exception e){
            throw new IllegalArgumentException(e);
        }finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz){
        Schema<T> schema = RuntimeSchema.getSchema(clazz);

        T t = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes,t,schema);

        return t;
    }
}
