package com.lovezcy.netty.rpc.demo.builder;

import com.lovezcy.netty.rpc.api.RpcProvider;
import com.lovezcy.netty.rpc.api.impl.RpcProviderImpl;
import com.lovezcy.netty.rpc.demo.server.RaceTestService;
import com.lovezcy.netty.rpc.demo.server.impl.RaceTestServiceImpl;

public class ProviderBuilder {
    public static void main(String[] args) {
        RpcProvider rpcProvider = new RpcProviderImpl();

        rpcProvider.serviceInterface(RaceTestService.class)
                .version("1.0")
                .timeout(5000)
                .impl(new RaceTestServiceImpl())
                .publish();
    }

    public int add(int i, int j){
        return i+j;
    }
}
