package com.lovezcy.netty.rpc.async;

import com.alibaba.fastjson.JSON;
import com.lovezcy.netty.rpc.model.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 调用结果异步响应
 * @author dingzhaolei
 * @date 2019/2/24 20:49
 **/
@Slf4j
public class ResponseFuture {
    public static final ThreadLocal<Future<Object>> FUTURE_THREAD_LOCAL = new ThreadLocal<>();

    public static Object getResponse(long timeout) throws InterruptedException{
        log.info("Thread [" + Thread.currentThread() + "]  get the response future req timeout:{}",timeout);
        if (FUTURE_THREAD_LOCAL.get() == null){
            throw new RuntimeException("Thread [" + Thread.currentThread() + "] have not set the response future!");
        }

        try {
            RpcResponse response = (RpcResponse) FUTURE_THREAD_LOCAL.get().get(timeout, TimeUnit.MILLISECONDS);
            log.info("Thread [" + Thread.currentThread() + "]  get the response future res:{}",response);
            return response.getAppResponse();
        }catch (ExecutionException e){
            throw new RuntimeException(e);
        }catch (TimeoutException e){
            throw new RuntimeException("time out",e);
        }catch (InterruptedException e){
            throw new RuntimeException(" thread interrupte", e);
        }
    }

    public static void setFuture(Future<Object> future){
        log.info("Thread [" + Thread.currentThread() + "]  set the response future:{}", JSON.toJSON(future));
        FUTURE_THREAD_LOCAL.set(future);
    }
}
