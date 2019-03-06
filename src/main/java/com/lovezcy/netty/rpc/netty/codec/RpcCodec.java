package com.lovezcy.netty.rpc.netty.codec;

import com.lovezcy.netty.rpc.model.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * 编码解码器
 * @author dingzhaolei
 * @date 2019/3/5 16:13
 **/
public class RpcCodec extends MessageToMessageCodec<ByteBuf,Object> {
    private static Object responseCacheName = null;
    private static byte[] responseCacheValue = null;
    private static Object requestCacheName = null;
    private static byte[] requestCacheValue = null;

    private Class<?> genericClass;

    public RpcCodec(Class<?> genericClass){
        this.genericClass = genericClass;
    }


    @Override
    protected  void encode(ChannelHandlerContext ctx, Object msg, List<Object> out)
            throws Exception{
        if (genericClass.equals(RpcResponse.class)){
            //先查缓存
            RpcResponse response = (RpcResponse)msg;
            String requestId = response.getRequestId();

            response.setRequestId("");

            if (requestCacheName.equals(requestCacheName)){
                //TODO
            }
        }

    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
            throws Exception{

    };
}
