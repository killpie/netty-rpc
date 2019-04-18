package com.lovezcy.netty.rpc.demo.server.impl;

import com.lovezcy.netty.rpc.demo.builder.ConsumerBuilder;
import com.lovezcy.netty.rpc.demo.util.ConsumerTestLog;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 压测
 */
@Slf4j
public class ConsumerPerformanceTest {
    static AtomicInteger atomicInteger = new AtomicInteger(0);
    final static int time = 10;
    public static void main(String[] args) throws Exception{
        int coreCount =Runtime.getRuntime().availableProcessors()*2;
        final ExecutorService service = Executors.newFixedThreadPool(coreCount);

        ConsumerBuilder consumerBuilder = new ConsumerBuilder();
        final Method method = consumerBuilder.getClass().getMethod("pressureTest",null);
        final CountDownLatch latch = new CountDownLatch(coreCount);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < coreCount; i++) {
            service.execute(()-> {
                while (atomicInteger.get()<time){
                    try {
                        if ((Boolean) method.invoke(consumerBuilder,null)){
                            atomicInteger.incrementAndGet();
                        }
                    }catch (Exception e){

                    }
                }
            });

            latch.countDown();
        }

        long endTime = System.currentTimeMillis();

        float qps = (endTime-startTime)/atomicInteger.get();
        ConsumerTestLog.getPerformanceOutputStream().write(("qps:"+qps).getBytes());
        ConsumerTestLog.getPerformanceOutputStream().close();

    }
}
