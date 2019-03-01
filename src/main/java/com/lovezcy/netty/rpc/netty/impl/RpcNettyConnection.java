package com.lovezcy.netty.rpc.netty.impl;

import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import com.lovezcy.netty.rpc.model.RpcRequest;
import com.lovezcy.netty.rpc.netty.InvokeFuture;
import com.lovezcy.netty.rpc.netty.ResultFuture;
import com.lovezcy.netty.rpc.netty.RpcClientHandler;
import com.lovezcy.netty.rpc.netty.RpcConnection;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * rpc连接的具体实现
 * @author dingzhaolei
 * @date 2019/3/1 13:34
 **/
public class RpcNettyConnection implements RpcConnection {
    private InetSocketAddress inetSocketAddress;
    private volatile Channel channel;
    private RpcClientHandler handler;



    @Override
    public void init() {

    }

    @Override
    public void connect() {

    }

    @Override
    public void connect(String host, int port) {

    }

    @Override
    public Object send(RpcRequest request, boolean async) {
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
