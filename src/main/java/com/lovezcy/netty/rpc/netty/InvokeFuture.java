package com.lovezcy.netty.rpc.netty;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 异步调用
 * @author dingzhaolei
 * @date 2019/2/24 22:21
 **/
public class InvokeFuture<T> {
    private Semaphore semaphore = new Semaphore(0);
    @Getter
    private Throwable cause;
    private T result;
    private List<InvokeListener<T>> listeners = new ArrayList<>();
    @Setter
    @Getter
    private String method;
    private boolean relase;

    public InvokeFuture(){
    }

    public void setResult(T result){
        this.result = result;
        notifyListeners();
        synchronized (semaphore){
            if (!relase){
                semaphore.release(Integer.MAX_VALUE-semaphore.availablePermits());
                relase = true;
            }
        }
    }


    public T getResult(long timeout, TimeUnit timeUnit){
        try{
            if (semaphore.tryAcquire(timeout,timeUnit)){
                throw new RuntimeException("获得凭证失败");
            }
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
        if (cause!=null){
            throw new RuntimeException(this.cause);
        }

        return result;
    }

    public void setCause(Throwable cause){
        this.cause = cause;
        notifyListeners();
        if (!relase){
            semaphore.release(Integer.MAX_VALUE-semaphore.availablePermits());
            relase = true;
        }
    }

    public void addInvokeListener(InvokeListener<T> listener){
        listeners.add(listener);
    }


    private void notifyListeners(){
        for (InvokeListener<T> listener : listeners){
            if (cause != null){
                listener.failure(cause);
            }else {
                listener.success(result);
            }
        }
    }

}


interface InvokeListener<T>{
    void success(T t);
    void failure(Throwable e);
}