package com.lovezcy.netty.rpc.netty;

import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import com.lovezcy.netty.rpc.model.protocol.RpcRequest;

import java.util.List;
import java.util.Map;

/**
 *描述与服务器的连接
 * @author dingzhaolei
 * @date 2019/2/24 22:15
 **/
public interface RpcConnection {
    void init();
    void connect();
    void connect(String host, int port);
    Object send(RpcRequest request, boolean async);
    void close();
    boolean isClose();
    boolean isConnected();
    public boolean containsFuture(String key);
    public InvokeFuture<Object> removeFuture(String key);
    public void setResult(Object result);
    public void setTimeOut(long timeOut);
    public void setAsyncMethod(Map<String, ResponseCallbackListener> map);
    public List<ResultFuture> getFutures(String method);
}
