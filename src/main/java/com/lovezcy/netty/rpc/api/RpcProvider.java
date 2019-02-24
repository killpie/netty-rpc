package com.lovezcy.netty.rpc.api;

/**
 * 提供者
 * @author dingzhaolei
 * @date 2019/2/24 21:12
 **/
public class RpcProvider {
    public RpcProvider(){
    }

    private void init(){

    }

    /**
     * 设置对外暴露服务的接口
     * @param serviceInterface
     * @return
     */
    public RpcProvider serviceInterface(Class<?>  serviceInterface){
        return this;
    }


    public RpcProvider version(String version){
        return this;
    }


    public RpcProvider impl(Object serviceInstance){
        return this;
    }


    public RpcProvider timeout(int timeout){
        return this;
    }


    /**
     * 设置编码格式
     * @param serializeType
     * @return
     */
    public RpcProvider serializeType(String serializeType){
        return this;
    }


    /**
     * 发布服务
     */
    public void publish(){

    }

}
