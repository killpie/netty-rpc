package com.lovezcy.netty.rpc.demo.builder;

import com.lovezcy.netty.rpc.api.RpcConsumer;
import com.lovezcy.netty.rpc.api.impl.RpcConsumerImpl;
import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import com.lovezcy.netty.rpc.async.ResponseFuture;
import com.lovezcy.netty.rpc.demo.server.RaceTestService;
import com.lovezcy.netty.rpc.demo.server.impl.RaceTestResponseCallback;
import com.lovezcy.netty.rpc.demo.server.impl.RaceTestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ConsumerBuilder {
    private static RaceTestService raceTestService;
    private static RpcConsumer rpcConsumer;
    AtomicInteger atomicInteger = new AtomicInteger();
    static{
        rpcConsumer=new RpcConsumerImpl("127.0.0.1",18883);

        //配置消费者
        raceTestService = (RaceTestService) rpcConsumer.interfaceClass(RaceTestService.class)
                .version("1.0")
                .instance();
        RaceTestService raceTestService_ttt = raceTestService;
        log.info("xx");
       // log.info("raceTestService1:{}",raceTestService);

    }

    @Test
    public void add(){
        log.info("1:"+Proxy.getInvocationHandler(raceTestService).toString());
        rpcConsumer.aysnCall("add");
        log.info("2:"+Proxy.getInvocationHandler(raceTestService).toString());
        int d = raceTestService.add(2,2);
        rpcConsumer.cancelAsyn("add");
        Assert.assertEquals(4,d);
    }

    @Test
    public void aysnCallAdd(){
        rpcConsumer.aysnCall("getString");
        String f = raceTestService.getString();
        try {
            String result = (String) ResponseFuture.getResponse(6000);
            Assert.assertEquals("I am proxy", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rpcConsumer.cancelAsyn("getString");
        }
    }


    public String pressureTest(){
        String f = raceTestService.getString();
        //Assert.assertEquals("I am proxy", f);
        return f;
    }



}
