package com.lovezcy.netty.rpc.aop;


import com.lovezcy.netty.rpc.model.protocol.RpcRequest;

public interface ConsumerHook {
    void before(RpcRequest request);
    void after(RpcRequest request);
}
