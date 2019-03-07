package com.lovezcy.netty.rpc.netty.codec;

import com.lovezcy.netty.rpc.model.RpcRequest;
import com.lovezcy.netty.rpc.model.RpcResponse;
import com.lovezcy.netty.rpc.tool.Tool;
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
    private static Object enResponseCacheName = null;
    private static byte[] enResponseCacheValue = null;
    private static Object enRequestCacheName = null;
    private static byte[] enRequestCacheValue = null;

    private static byte[] deRequestCacheName=null;
    private static RpcRequest deRequestCacheValue=null;
    private static byte[] deResponseCacheName=null;
    private static RpcResponse deResponseCacheValue=null;

    private Class<?> genericClass;

    public RpcCodec(Class<?> genericClass){
        this.genericClass = genericClass;
    }


    @Override
    protected  void encode(ChannelHandlerContext ctx, Object msg, List<Object> out)
            throws Exception{
        ByteBuf byteBuf = ctx.alloc().ioBuffer();

        if (genericClass.equals(RpcResponse.class)){
            //先查缓存
            RpcResponse response = (RpcResponse)msg;
            String requestId = response.getRequestId();

            response.setRequestId("");
            byte[] requestIdByte = requestId.getBytes();
            byte[] body = null;

            if (enResponseCacheName!=null && enResponseCacheName.equals(response)){
                body = enResponseCacheValue;
            }else {
                body = Tool.serialize(msg);
                enResponseCacheName = response;
                enResponseCacheValue = body;
            }
            // [|总长度||requestId长度||request字节内容||响应内容|]
            int totalLen = 4+4+requestIdByte.length+body.length;
            byteBuf.writeInt(totalLen);
            byteBuf.writeInt(requestIdByte.length);
            byteBuf.writeBytes(requestIdByte);
            byteBuf.writeBytes(body);
            out.add(byteBuf);
            return;
        }

        if (genericClass.equals(RpcRequest.class)){
            //先查缓存
            RpcRequest request = (RpcRequest)msg;
            String requestId = request.getRequestId();

            request.setRequestId("");
            byte[] requestIdByte = requestId.getBytes();
            byte[] body = null;

            if (enRequestCacheName!=null && enRequestCacheName.equals(request)){
                body = enRequestCacheValue;
            }else {
                body = Tool.serialize(msg);
                enRequestCacheName = request;
                enRequestCacheValue = body;
            }

            // [|总长度||requestId长度||request字节内容||响应内容|]
            int totalLen = 4+4+requestIdByte.length+body.length;
            byteBuf.writeInt(totalLen);
            byteBuf.writeInt(requestIdByte.length);
            byteBuf.writeBytes(requestIdByte);
            byteBuf.writeBytes(body);
            out.add(byteBuf);
            return;
        }

        byte[] body = Tool.serialize(msg);
        byteBuf.writeInt(body.length);
        byteBuf.writeBytes(body);
        out.add(byteBuf);
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception{
        int HEAD_LENGTH=4;
        if (in.readableBytes() < HEAD_LENGTH) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        if(genericClass.equals(RpcResponse.class)) {
            int requestIdLength=in.readInt();//获取到requestId的长度

            byte[] requestIdBytes=new byte[requestIdLength];
            in.readBytes(requestIdBytes);

            int bodyLength=dataLength-4-requestIdLength;

            byte[] body = new byte[bodyLength];
            in.readBytes(body);
            String requestId=new String(requestIdBytes);

            if(deResponseCacheName!=null&&cacheEqual(deResponseCacheName,body)) {
                RpcResponse obj=new RpcResponse();
                obj.setRequestId(requestId);
                obj.setAppResponse(deResponseCacheValue.getAppResponse());
                obj.setClazz(deResponseCacheValue.getClazz());
                obj.setException(deResponseCacheValue.getException());
                out.add(obj);
            }
            else {
                RpcResponse obj=(RpcResponse) Tool.deserialize(body, genericClass);
                obj.setRequestId(requestId);//设置requestId
                out.add(obj);

                deResponseCacheName=body;
                deResponseCacheValue=new RpcResponse();
                deResponseCacheValue.setAppResponse(obj.getAppResponse());
                deResponseCacheValue.setClazz(obj.getClazz());
                deResponseCacheValue.setException(obj.getException());

            }
        }
        else if(genericClass.equals(RpcRequest.class))
        {
            int requestIdLength=in.readInt();//获取到requestId的长度

            byte[] requestIdBytes=new byte[requestIdLength];
            in.readBytes(requestIdBytes);

            int bodyLength=dataLength-4-requestIdLength;

            byte[] body = new byte[bodyLength];
            in.readBytes(body);
            String requestId=new String(requestIdBytes);

            if(deRequestCacheName!=null&&cacheEqual(deRequestCacheName,body))
            {
                RpcRequest obj=new RpcRequest();
                obj.setClassName(deRequestCacheValue.getClassName());
                obj.setContext(deRequestCacheValue.getContext());
                obj.setMethodName(deRequestCacheValue.getMethodName());
                obj.setParameters(deRequestCacheValue.getParameters());
                obj.setParameterTypes(deRequestCacheValue.getParameterTypes());
                obj.setRequestId(requestId);

                out.add(obj);

            }
            else
            {
                RpcRequest obj=(RpcRequest) Tool.deserialize(body, genericClass);
                obj.setRequestId(requestId);//设置requestId
                out.add(obj);

                deRequestCacheName=body;
                deRequestCacheValue=new RpcRequest();
                deRequestCacheValue.setClassName(obj.getClassName());
                deRequestCacheValue.setContext(obj.getContext());
                deRequestCacheValue.setMethodName(obj.getMethodName());
                deRequestCacheValue.setParameters(obj.getParameters());
                deRequestCacheValue.setParameterTypes(obj.getParameterTypes());
            }
        }
        else
        {
            byte[] body = new byte[dataLength];
            in.readBytes(body);
            Object obj=Tool.deserialize(body, genericClass);
            out.add(obj);
        }
    }


    private static boolean cacheEqual(byte[] data1,byte[] data2)
    {
        if(data1==null)
        {
            if(data2!=null)
                return false;
        }
        else
        {
            if(data2==null)
                return false;

            if(data1.length!=data2.length)
                return false;

            for (int i = 0; i < data1.length; i++) {
                if(data1[i]!=data2[i])
                    return false;
            }
        }
        return true;
    }


}
