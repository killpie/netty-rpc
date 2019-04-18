package com.lovezcy.netty.rpc.demo.builder;

import com.lovezcy.netty.rpc.api.RpcConsumer;
import com.lovezcy.netty.rpc.api.impl.RpcConsumerImpl;
import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import com.lovezcy.netty.rpc.demo.server.RaceTestService;
import com.lovezcy.netty.rpc.demo.server.impl.RaceTestResponseCallback;
import com.lovezcy.netty.rpc.demo.server.impl.RaceTestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ConsumerBuilder {
    static RaceTestService raceTestService;
    static RpcConsumer rpcConsumer;
    AtomicInteger atomicInteger = new AtomicInteger();
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
        Assert.assertEquals(4,d);
    }

    public void pressureTest() throws InterruptedException{
        ResponseCallbackListener listener = new RaceTestResponseCallback();
        rpcConsumer.aysnCall("add",listener);
        int  d = atomicInteger.getAndIncrement();
        raceTestService.add(d,d);
        Object result = ((RaceTestResponseCallback) listener).getResponse();
        Assert.assertEquals(d*2,Integer.parseInt((String) result));
    }



}
