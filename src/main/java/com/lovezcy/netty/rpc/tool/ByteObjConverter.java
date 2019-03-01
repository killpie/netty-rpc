package com.lovezcy.netty.rpc.tool;

import java.io.*;

/**
 * 字节转换工具类
 * @author dingzhaolei
 * @date 2019/3/1 17:04
 **/
public class ByteObjConverter {
    public static Object ByteToObject(byte[] bytes) {
        Object obj = null;


        try (
                ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
                ObjectInputStream oi =  new ObjectInputStream(bi);
                ){

            obj = oi.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static byte[] ObjectToByte(Object obj) {
        byte[] bytes = null;

        try (
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream oo =  new ObjectOutputStream(bo);
                ){

            oo.writeObject(obj);
            bytes = bo.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
