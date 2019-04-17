package com.lovezcy.netty.rpc.demo.builder;

import com.lovezcy.netty.rpc.api.RpcConsumer;
import com.lovezcy.netty.rpc.api.impl.RpcConsumerImpl;
import com.lovezcy.netty.rpc.demo.server.RaceTestService;
import com.lovezcy.netty.rpc.demo.server.impl.RaceTestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class ConsumerBuilder {
    static RaceTestService raceTestService;
    static RpcConsumer rpcConsumer;
    static{
        rpcConsumer=new RpcConsumerImpl("127.0.0.1",18888);

        //配置消费者
        raceTestService = (RaceTestService) rpcConsumer.interfaceClass(RaceTestService.class)
                .version("1.0")
                .instance();

    }

    @Test
    public void add(){
        int d = raceTestService.add(2,2);
        log.info("raceTestService:{}",d);
        Assert.assertEquals(4,d);
    }



}
