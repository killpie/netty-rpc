package com.lovezcy.netty.rpc.aop;


import com.lovezcy.netty.rpc.model.protocol.RpcRequest;

public interface ConsumerHook {
    public void before(RpcRequest request);
    public void after(RpcRequest request);
}
