package com.lovezcy.netty.rpc.api.impl;

import com.lovezcy.netty.rpc.context.RpcContext;
import com.lovezcy.netty.rpc.model.protocol.RpcRequest;
import com.lovezcy.netty.rpc.model.protocol.RpcResponse;
import com.lovezcy.netty.rpc.tool.ByteObjConverter;
import com.lovezcy.netty.rpc.tool.Tool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理服务器收到的rpc请求并返回结果
 */
@Slf4j
public class RpcRequestHandler extends ChannelInboundHandlerAdapter {
    //保存每个请求的ID及其端口号所对应的RpcContext的Map
    private static Map<String,Map<String ,Object>> threadLocalMap = new ConcurrentHashMap<>();
    //服务器接口-实现类的映射表
    private final Map<String,Object> handlerMap;
    private Map<String, FastMethod> methodMap=new ConcurrentHashMap<>();

    public RpcRequestHandler(Map<String ,Object> handlerMap){
        this.handlerMap = handlerMap;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive id:{}",ctx.channel().id());
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive id:{}",ctx.channel().id());
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest)msg;
        String host = ctx.channel().remoteAddress().toString();
        updateRpcContext(host,request.getContext());

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        try{
            Object result = handle(request);
            response.setAppResponse(ByteObjConverter.ObjectToByte(result));
        }catch (Throwable t){
            response.setException(Tool.serialize(t));
            response.setClazz(t.getClass());
        }
        ctx.writeAndFlush(response);
    }

    private Object handle(RpcRequest request){
        String className = request.getClassName();
        //TODO 后期考虑实现别名机制
        Object classimpl = handlerMap.get(className);

        Class<?> clazz = classimpl.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Object methodValue = null;
        try {
            if (methodMap.containsKey(methodName)){
                methodValue = methodMap.get(methodName).invoke(classimpl,parameters);
            }else {
                FastClass serviceFastClass = FastClass.create(clazz);
                FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName,parameterTypes);
                methodMap.put(methodName,serviceFastMethod);
                methodValue = serviceFastMethod.invoke(classimpl,parameters);
            }
        }catch (InvocationTargetException e){
            String s = String.format("class:{},method:{},执行异常",clazz.getName(),methodName);
            throw new RuntimeException(s);
        }

        if (methodValue == null){
            throw new RuntimeException("methodValue 不能为 null");
        }

        return methodValue;

    }

    private void updateRpcContext(String host,Map<String ,Object> map){
        Map<String ,Object> local = threadLocalMap.get(host);
        if (local != null){
            //加载客户端map
            map.putAll(local);
        }
        threadLocalMap.put(host,map);
        for (Map.Entry<String ,Object> entry:map.entrySet()
        ) {
            RpcContext.addProp(entry.getKey(),entry.getValue());
        }
    }
}