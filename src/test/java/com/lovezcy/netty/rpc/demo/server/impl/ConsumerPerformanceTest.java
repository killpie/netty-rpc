package com.lovezcy.netty.rpc.demo.server.impl;

import com.lovezcy.netty.rpc.demo.builder.ConsumerBuilder;
import com.lovezcy.netty.rpc.demo.util.ConsumerTestLog;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import java.io.FileOutputStream;
import java.io.OutputStream;
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
    final static int time =1000;
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
        }

        service.isShutdown();
        try{
         //   latch.await(Integer.MAX_VALUE, TimeUnit.NANOSECONDS);
            service.awaitTermination(Integer.MAX_VALUE,TimeUnit.NANOSECONDS);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        long endTime = System.currentTimeMillis();

        double temp = atomicInteger.get()/1.0;
        double tps = temp/(endTime-startTime);
        try {
            OutputStream f = ConsumerTestLog.getPerformanceOutputStream();
            String d = "总耗时:"+(endTime-startTime)+"ms\n";
            d = d+"并发量:"+atomicInteger.get()+"\n";
            d = d+"TPS:"+tps*1000+" 单位每秒";
            f.write((d.getBytes()));
            f.flush();
        }catch (Exception e){

        }


    }
}
