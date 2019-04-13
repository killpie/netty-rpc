package com.lovezcy.netty.rpc.api.impl;

import com.lovezcy.netty.rpc.aop.ConsumerHook;
import com.lovezcy.netty.rpc.api.RpcConsumer;
import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import com.lovezcy.netty.rpc.context.RpcContext;
import com.lovezcy.netty.rpc.model.protocol.RpcRequest;
import com.lovezcy.netty.rpc.model.protocol.RpcResponse;
import com.lovezcy.netty.rpc.netty.RpcConnection;
import com.lovezcy.netty.rpc.netty.impl.RpcNettyConnection;
import com.lovezcy.netty.rpc.tool.Tool;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * rpc消费者实现类
 */
public class RpcConsumerImpl extends RpcConsumer {
    private static AtomicLong callTimes = new AtomicLong(0);
    private RpcConnection connection;
    private List<RpcConnection> connectionList;
    private Map<String, ResponseCallbackListener> asyncMethods;
    @Getter
    private Class<?> interfaceCalss;

    @Getter
    private String version;
    private int timeout;
    @Getter
    private ConsumerHook hook;

    public int getTimeout(int timeout){
        this.connection.setTimeOut(timeout);
        return timeout;
    }

    RpcConnection select(){
        //TODO 考虑优化负载均衡算法

        if (connectionList == null || connectionList.size() == 0){
            throw new RuntimeException("rpc连接数目为0");
        }

        if (connectionList.size() == 1){
            return connection;
        }
        int d = (int)(callTimes.getAndIncrement()%connectionList.size());

        return connectionList.get(d);
    }


    public RpcConsumerImpl(String host, int port){

    }

    private void init(String host, int port){
        this.connection = new RpcNettyConnection(host,port);
        this.asyncMethods = new HashMap<>();
        this.connection.connect();
        connectionList = new ArrayList<>();
        int num = Runtime.getRuntime().availableProcessors()-1;

        for (int i = 0; i < num; i++) {
            RpcConnection rpcConnection = new RpcNettyConnection(host,port);
            connectionList.add(rpcConnection);
            rpcConnection.connect();
        }
    }

    public void destroy() throws Exception{
        if (connection != null){
            connection.close();
        }
    }

    public <T> T proxy(Class<T> interfaceCalss) throws Exception{
        if (!interfaceCalss.isInterface()){
            throw new IllegalArgumentException("所需代理创建的类不能为接口");
        }

        return (T) Proxy.newProxyInstance(interfaceCalss.getClassLoader(),
                new Class[]{interfaceCalss},this);
    }

    @Override
    public RpcConsumer interfaceClass(Class<?> interfaceClass){
        this.interfaceCalss = interfaceClass;
        return this;
    }

    @Override
    public RpcConsumer version(String version){
        this.version = version;
        return this;
    }

    @Override
    public RpcConsumer clientTimout(int clientTimeout){
        this.timeout = clientTimeout;
        return this;
    }

    @Override
    public RpcConsumer hook(ConsumerHook hook){
        this.hook = hook;
        return this;
    }

    public Object instance(){
        try{
            return proxy(this.interfaceCalss);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void aysnCall(String methodName){
        aysnCall(methodName,null);
    }


    @Override
    public <T extends ResponseCallbackListener> void aysnCall(
            String methodName, T callbackListener){
        this.asyncMethods.put(methodName,callbackListener);
        this.connection.setAsyncMethod(asyncMethods);

        for (RpcConnection c:connectionList
             ) {
            c.setAsyncMethod(asyncMethods);
        }
    }

    @Override
    public void cancelAsyn(String method){
        this.asyncMethods.remove(method);
        this.connection.setAsyncMethod(asyncMethods);

        for (RpcConnection c:connectionList
        ) {
            c.setAsyncMethod(asyncMethods);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();

        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        if (hook != null){
            hook.before(request);
        }

        RpcResponse response = null;
        try{
            request.setContext(RpcContext.getProps());
            response =(RpcResponse) select().send(request,asyncMethods.containsKey(request.getMethodName()));

            if (hook != null){
                hook.after(request);
            }
        }
        catch (Throwable t) {
            throw t;
        }

        if(response==null) {
            return null;
        } else if (response.getErrorMsg() != null) {
            throw response.getErrorMsg();
        } else {
            return response.getAppResponse();
        }
    }


}
