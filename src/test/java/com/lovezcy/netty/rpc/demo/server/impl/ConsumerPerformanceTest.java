package com.lovezcy.netty.rpc.demo.server.impl;

import com.lovezcy.netty.rpc.demo.builder.ConsumerBuilder;
import com.lovezcy.netty.rpc.demo.util.ConsumerTestLog;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 压测
 */
@Slf4j
public class ConsumerPerformanceTest {
    static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static Method method;
    private static ConsumerBuilder consumerBuilder;
    private static CountDownLatch latch;
    final static int time = 100;
    public static void main(String[] args) throws Exception{
        test();
    }


    public static void test(){
        int coreCount =Runtime.getRuntime().availableProcessors()*2;
        final ExecutorService service = Executors.newFixedThreadPool(coreCount);

        consumerBuilder = new ConsumerBuilder();
        try {
            method = consumerBuilder.getClass().getDeclaredMethod("pressureTest",null);
        }catch (NoSuchMethodException e){

        }
        latch = new CountDownLatch(coreCount);
        long startTime = System.currentTimeMillis() ;
        for (int i = 0; i < coreCount; i++) {
            service.execute(()-> {
                    while (atomicInteger.get()<time){
                        try {
                            String d = (String) method.invoke(consumerBuilder,null);
                            if ("I am proxy".equals(d)){
                                atomicInteger.incrementAndGet();
                                log.info("计数器:{}",atomicInteger.get());
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
            });

            latch.countDown();
            log.info("************latch.getCount():{}",latch.getCount());
        }

        try{
            latch.await(30, TimeUnit.SECONDS);
            service.awaitTermination(3000,TimeUnit.MILLISECONDS);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        long endTime = System.currentTimeMillis();

        float qps = (endTime-startTime)/atomicInteger.get();
        try {
            ConsumerTestLog.getPerformanceOutputStream().write(("qps:"+qps).getBytes());
            ConsumerTestLog.getPerformanceOutputStream().close();
        }catch (Exception e){

        }


    }
}
