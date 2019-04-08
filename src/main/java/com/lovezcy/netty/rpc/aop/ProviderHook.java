package com.lovezcy.netty.rpc.aop;


import com.lovezcy.netty.rpc.model.protocol.RpcRequest;

/**
 * @author dingzhaolei
 * @date 2019/2/21 11:38
 **/
public interface ProviderHook {
    void before(RpcRequest request);
    void after(RpcRequest request);
}
