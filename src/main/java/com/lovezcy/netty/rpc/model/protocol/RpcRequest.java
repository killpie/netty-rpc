package com.lovezcy.netty.rpc.model.protocol;

import com.lovezcy.netty.rpc.constant.DatagramFormatEnum;
import com.lovezcy.netty.rpc.model.Packet;
import lombok.Data;


import java.io.Serializable;
import java.util.Map;

/**
 * @author dingzhaolei
 * @date 2019/2/20 10:33
 **/
@Data
public class RpcRequest extends Packet implements Serializable {
    private static final long serialVersionUID = 5606111910428846773L;

    private String appId;
    private String token;
    private String requestId;
    private String className;
    private String methodName;
    private Class<?> parameterTypes;
    private Object[] parameters;
    private Map<String,Object> context;

    @Override
    public byte getCommand(){
        return DatagramFormatEnum.RPC_REQUEST.getKey();
    }


}
