package com.lovezcy.netty.rpc.api.impl;

import com.alibaba.fastjson.JSON;
import com.lovezcy.netty.rpc.api.RpcProvider;
import com.lovezcy.netty.rpc.netty.codec.RpcCodec;
import com.lovezcy.netty.rpc.netty.codec.Spliter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcProviderImpl extends RpcProvider {
    private Map<String,Object> handlerMap = new ConcurrentHashMap<>();
    @Getter
    private Class<?> interfaceClazz;
    @Getter
    private Object classImpl;
    @Getter
    private String version;
    @Getter
    private int timeOut;
    @Getter
    private String type;

    /**
     * 设置对外暴露服务的接口
     * @param serviceInterface
     * @return
     */
    public RpcProvider serviceInterface(Class<?>  serviceInterface){
        this.interfaceClazz = serviceInterface;
        return this;
    }


    public RpcProvider version(String version){
        this.version = version;
        return this;
    }


    public RpcProvider impl(Object serviceInstance){
        this.classImpl = serviceInstance;
        return this;
    }


    public RpcProvider timeout(int timeout){
        this.timeOut = timeout;
        return this;
    }


    /**
     * 设置编码格式
     * @param serializeType
     * @return
     */
    public RpcProvider serializeType(String serializeType){
        this.type = serializeType;
        return this;
    }


    /**
     * 发布服务
     */
    public void publish(){
        //TODO 后期可以考虑使用zookeeper
        log.info("RpcProviderImpl.publish className:{},classImpl:{}",interfaceClazz.getName(),classImpl);
        handlerMap.put(interfaceClazz.getName(),classImpl);
        log.info("RpcProviderImpl.publish handlerMap:{}", handlerMap.get(interfaceClazz.getName()));
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(new RpcCodec());
                        ch.pipeline().addLast(new RpcRequestHandler(handlerMap));
                    }
                }).option(ChannelOption.SO_KEEPALIVE,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_SNDBUF,1024)
                .option(ChannelOption.SO_RCVBUF,2048);

        try{
            ChannelFuture future = serverBootstrap.bind(18888).sync();
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()){
                        log.info("rpc服务端启动");
                    }else {
                        log.info("rpc服务端启动失败");
                    }
                }
            }).channel().closeFuture().sync();
        }catch (InterruptedException e){
            throw new RuntimeException("服务器启动失败");
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
