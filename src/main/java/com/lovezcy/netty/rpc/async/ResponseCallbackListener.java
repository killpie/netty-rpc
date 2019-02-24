package com.lovezcy.netty.rpc.async;

/**
 * 对异步回调结果的监听器
 * @author dingzhaolei
 * @date 2019/2/24 20:46
 **/
public interface ResponseCallbackListener {
    void onResponse(Object response);
    void onTimeout();
    void onException(Exception e);
}
