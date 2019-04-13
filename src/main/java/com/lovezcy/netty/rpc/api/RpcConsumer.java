package com.lovezcy.netty.rpc.api;

import com.lovezcy.netty.rpc.aop.ConsumerHook;
import com.lovezcy.netty.rpc.async.ResponseCallbackListener;
import sun.util.resources.cldr.ss.CalendarData_ss_SZ;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Calendar;

/**
 * 消费者
 * @author dingzhaolei
 * @date 2019/2/24 20:08
 **/
public class RpcConsumer implements InvocationHandler {

    private Class<?> interfacefaceClazz;

    public RpcConsumer(){
    }

    private void init(){}

    /**
     *设置消费者想要使用的接口，它会调用一个远程服务来获得这个接口方法的结果
     * @param interfacefaceClazz
     * @return
     */
    public RpcConsumer interfaceClass(Class<?> interfacefaceClazz){
        this.interfacefaceClazz = interfacefaceClazz;
        return this;
    }


    /**
     * 设置服务的版本
     * @param version
     * @return
     */
    public RpcConsumer version(String version){
        return this;
    }

    /**
     * 消费者设置优先（在生产者和消费者中同时设置超时时间（防止由于网络等原因造成的堵塞），
     * 消费者设置的超时时间生效）
     * @param clientTimeout
     * @return
     */
    public RpcConsumer clientTimout(int clientTimeout){
        return this;
    }

    /**
     * 注册该消费者
     * @param hook
     * @return
     */
    public RpcConsumer hook(ConsumerHook hook){
        return this;
    }

    /**
     * 返回一个代理对象
     * @return
     */
    public Object instance(){
        Object object = Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{this.interfacefaceClazz},this);
        return object;
    }


    public void aysnCall(String methodName){
    }

    /**
     *
     * @param methodName
     * @param callbackListener
     * @param <T>
     */
    public <T extends ResponseCallbackListener> void aysnCall(String methodName, T callbackListener){
    }


    public void cancelAsyn(String method){
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

    public void setInterfacefaceClazz(Class<?> interfacefaceClazz){
        this.interfacefaceClazz = interfacefaceClazz;
    }

    public Class<?> getInterfacefaceClazz(){
        return interfacefaceClazz;
    }








}
