package com.lovezcy.netty.rpc.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * @author dingzhaolei
 * @date 2019/2/24 22:21
 **/
public class InvokeFuture<T> {
    private Semaphore semaphore = new Semaphore(0);
    private Throwable cause;
    private T result;
    private List<InvokeListener<T>> listeners = new ArrayList<>();
    private String method;
}


interface InvokeListener<T>{
    void success(T t);
    void failure(Throwable e);
}