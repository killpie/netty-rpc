package com.lovezcy.netty.rpc.netty;

import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import com.lovezcy.netty.rpc.model.protocol.RpcResponse;
import com.lovezcy.netty.rpc.tool.ByteObjConverter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端控制器
 * @author dingzhaolei
 * @date 2019/3/1 14:36
 **/
@Slf4j
public class RpcClientHandler extends ChannelInboundHandlerAdapter {
    private static byte[] cacheName = null;
    private static Object cacheValue = null;
    private RpcConnection connection;
    private Throwable cause;
    private Map<String, ResponseCallbackListener> listenerMap;

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientHandler.class);

    public static boolean cacheEqual(byte[] data0, byte[] data1){
        if (data0 == null && data1 == null){
            return false;
        }

        if (data0 == null || data1 == null){
            return false;
        }

        if (data0.length != data1.length){
            return false;
        }

        for (int i = 0; i < data0.length; i++) {
            if (data0[i] != data1[i]){
                return false;
            }
        }
        return true;
    }

    public RpcClientHandler(RpcConnection connection){
        this.connection = connection;
        this.listenerMap = new HashMap<>();
    }

    private void notifyListenerResponse(String key, Object result){
        if (listenerMap != null && listenerMap.containsKey(key) && listenerMap.get(key) != null){
            listenerMap.get(key).onResponse(result);
            LOGGER.info("notify method:{}, result:{}",key,result);
        }
    }

    private void notifyListenerException(String key){
        if (listenerMap != null && listenerMap.containsKey(key) && listenerMap.get(key) != null){
            listenerMap.get(key).onException((Exception) cause);
            LOGGER.error("notify method:{}, cause:{}",key,cause);
        }
    }

    public void setAsynMethod(Map<String,ResponseCallbackListener> map){
        this.listenerMap.putAll(map);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)throws Exception{
        super.channelActive(ctx);
        LOGGER.info("connected on server:{}",ctx.channel().localAddress().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
        RpcResponse response = (RpcResponse) msg;
        log.info("RpcClientHandler.channelRead channelId:{},response:{}",ctx.channel().id(),response);
        String key = response.getRequestId();

        boolean flag = connection.containsFuture(key);
        if (!flag){
            log.info("RpcClientHandler  containsFuture:{} key:{}",flag,key);
            return;
        }

        InvokeFuture future = connection.removeFuture(key);
        if (future == null){
            return;
        }
        log.info("RpcClientHandler 1");
        if (this.cause!=null){
            future.setCause(cause);
            future.setResult(cause);
            notifyListenerException(future.getMethod());
            cause.printStackTrace();
        }
        log.info("RpcClientHandler 2");
        byte[] data = (byte[]) response.getAppResponse();
        if (data == null){
            return;
        }
        log.info("RpcClientHandler 3");
        if (cacheName != null && cacheEqual(data,cacheName)){
            response.setAppResponse(cacheValue);
        }else {
            cacheName = data;
            Object cacheValue0 = ByteObjConverter.ByteToObject(data);
            response.setAppResponse(cacheValue0);
            cacheValue = cacheValue0;
        }
        log.info("RpcClientHandler 4");
        future.setResult(response);
        this.connection.setResult(response);
        notifyListenerResponse(future.getMethod(),response.getAppResponse());
        for (InvokeFuture<Object> f : connection.getFutures(future.getMethod())) {
            f.setResult(response);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        this.cause=cause;
        log.info("RpcClientHandler.exceptionCaught error:{}",cause.getCause());

    }


}
