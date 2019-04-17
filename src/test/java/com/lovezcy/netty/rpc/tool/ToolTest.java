package com.lovezcy.netty.rpc.tool;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
@Slf4j
public class ToolTest {
    AtomicLong atomicLong = new AtomicLong(0);
    @Test
    public void test() throws InterruptedException,ExecutionException{
        BlockingDeque blockingDeque = new LinkedBlockingDeque<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20,
                20,5000, TimeUnit.MILLISECONDS,blockingDeque);
        Long start = System.currentTimeMillis();
        List<Future<Boolean>> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Future<Boolean> future = executor.submit(new LocalTask());
            list.add(future);
        }

        int count = 0;
        for (int i = 0; i < 10000; i++) {
            Boolean b = list.get(i).get();
            if (!b.booleanValue()){
                count++;
            }
        }
        log.info("解析错误次数：{}",count);
        Long end = System.currentTimeMillis();
        log.info("总耗时:{} ms",end - start);
    }


    class ProtostuffTask implements Callable<Boolean> {

        public Boolean call(){
            Long t = atomicLong.getAndIncrement();
            byte[] bytes = Tool.serialize(t);
            Long t1 = Tool.deserialize(bytes,Long.class);
            log.info("解析Long(t={},t1={}),状态：{}",t,t1,t.intValue() == t1.intValue());

            return t.intValue() == t1.intValue();
        }
    }

    class LocalTask implements Callable<Boolean> {

        public Boolean call(){
            Long t = atomicLong.getAndIncrement();
            byte[] bytes = ByteObjConverter.ObjectToByte(t);
            Long t1 = (Long) ByteObjConverter.ByteToObject(bytes);
            log.info("解析Long(t={},t1={}),状态：{}",t,t1,t.intValue() == t1.intValue());

            return t.intValue() == t1.intValue();
        }
    }
}
