package com.lovezcy.netty.rpc.netty.impl;

import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import com.lovezcy.netty.rpc.model.protocol.RpcRequest;
import com.lovezcy.netty.rpc.netty.InvokeFuture;
import com.lovezcy.netty.rpc.netty.ResultFuture;
import com.lovezcy.netty.rpc.netty.RpcClientHandler;
import com.lovezcy.netty.rpc.netty.RpcConnection;
import com.lovezcy.netty.rpc.netty.codec.RpcCodec;
import com.lovezcy.netty.rpc.netty.codec.Spliter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc连接的具体实现
 * @author dingzhaolei
 * @date 2019/3/1 13:34
 **/
public class RpcNettyConnection implements RpcConnection {
    private InetSocketAddress inetSocketAddress;
    private volatile Channel channel;
    private RpcClientHandler handler;
    private static Map<String,InvokeFuture<Object>> futureMap = PlatformDependent.newConcurrentHashMap();
    private Map<String , Channel> channelMap = new ConcurrentHashMap<>();
    private Bootstrap bootstrap;
    //异步调用的时候的结果集
    private volatile ResultFuture<Object> resultFuture;
    private long timeOut = 3000;

    private boolean connected = false;

    public RpcNettyConnection(){

    }

    public RpcNettyConnection(String host, int port){
        inetSocketAddress = new InetSocketAddress(host,port);
        handler = new RpcClientHandler(this);
    }

    public Channel getChannel(String key){
        return channelMap.get(key);
    }


    @Override
    public void init() {
        try {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel){
                            channel.pipeline().addLast(new Spliter());
                            channel.pipeline().addLast(new RpcCodec());
                            channel.pipeline().addLast(handler);
                        }
                    }).option(ChannelOption.SO_KEEPALIVE,true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            ChannelFuture future = bootstrap.connect(this.inetSocketAddress).sync();
            Channel channel = future.channel();
            channelMap.put(this.inetSocketAddress.toString(),channel);
            connected = true;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void connect(String host, int port) {
        this.inetSocketAddress = new InetSocketAddress(host,port);
        ChannelFuture future = bootstrap.connect(inetSocketAddress);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                channelMap.put(channel.remoteAddress().toString(),channel);
            }
        });
    }

    @Override
    public Object send(RpcRequest request, boolean async) throws Exception{
        if (request == null || StringUtil.isNullOrEmpty(request.getClassName())|| StringUtil.isNullOrEmpty(request.getMethodName())){
            throw new Exception("request must not be null");
        }

        if (channel == null){
            channel = channelMap.get(this.inetSocketAddress.toString());
            if (channel!=null){
                final InvokeFuture<Object> future = new InvokeFuture<>();
                future.setMethod(request.getMethodName());
                futureMap.put(request.getRequestId(),future);

                ChannelFuture channelFuture = channel.writeAndFlush(request);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture cfuture) throws Exception {
                        if (!cfuture.isSuccess()){
                            future.setCause(cfuture.cause());
                        }
                    }
                });
            }
        }
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClose() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean containsFuture(String key) {
        return false;
    }

    @Override
    public InvokeFuture<Object> removeFuture(String key) {
        return null;
    }

    @Override
    public void setResult(Object result) {

    }

    @Override
    public void setTimeOut(long timeOut) {

    }

    @Override
    public void setAsyncMethod(Map<String, ResponseCallbackListener> map) {

    }

    @Override
    public List<ResultFuture> getFutures(String method) {
        return null;
    }
}
