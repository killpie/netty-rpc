package com.lovezcy.netty.rpc.demo.server.impl;

import com.alibaba.fastjson.JSON;
import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class RaceTestResponseCallback implements ResponseCallbackListener {

    CountDownLatch latch = new CountDownLatch(1);
    private Object response;

    public Object getResponse(){
        try {
            if(latch.await(3000, TimeUnit.MILLISECONDS)){
                return response;
            }
        }catch (InterruptedException e){

        }finally {
            return null;
        }
    }

    @Override
    public void onResponse(Object response) {
        log.info("RaceTestResponseCallback.onResponse response:{}", JSON.toJSON(response));
        this.response = response;
        latch.countDown();
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public void onException(Exception e) {

    }
}
