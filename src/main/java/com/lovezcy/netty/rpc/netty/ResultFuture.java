package com.lovezcy.netty.rpc.netty;

import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 异步结果类
 * @author dingzhaolei
 * @date 2019/2/25 11:20
 **/
public class ResultFuture<T> implements Future<Object> {
    private Semaphore semaphore = new Semaphore(0);
    @Getter
    private Throwable cause;
    private T result;
    private List<ResponseCallbackListener> listeners = new ArrayList<>();
    @Getter
    @Setter
    private boolean isDone;
    private long timeOut;
    @Getter
    @Setter
    private String requestId;

    public ResultFuture(long timeOut){
        this.timeOut = timeOut;
    }

    public void setResult(T result){
        this.result = result;
        this.isDone = true;
        notifyListener();
        semaphore.release(Integer.MAX_VALUE-semaphore.availablePermits());
    }

    public void setCause(Throwable cause){
        this.cause = cause;
        notifyListener();
        semaphore.release(Integer.MAX_VALUE-semaphore.availablePermits());
    }

    public void addListener(ResponseCallbackListener listener){
        listeners.add(listener);
    }

    /**
     * 通知监听该事件的监听器
     */
    private void  notifyListener(){
        for (ResponseCallbackListener listener : listeners) {
            if (cause!=null){
                listener.onException((Exception) cause);
            }else {
                listener.onResponse(result);
            }
        }
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        try{
            if (!semaphore.tryAcquire(timeOut,TimeUnit.SECONDS)){
                throw new RuntimeException("获取凭证超时");
            }
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        if (cause != null){
            throw new RuntimeException(cause);
        }

        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try{
            if (!semaphore.tryAcquire(timeout,unit)){
                throw new RuntimeException("获取凭证超时");
            }
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        if (cause != null){
            throw new RuntimeException(cause);
        }

        return result;
    }
}
