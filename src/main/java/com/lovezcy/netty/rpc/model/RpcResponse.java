package com.lovezcy.netty.rpc.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dingzhaolei
 * @date 2019/2/20 10:51
 **/
@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = -3888703143177777864L;
    private String requestId;
    private Class<?> clazz;
    private byte[] exception;
    private Throwable errorMsg;
    private Object appResponse;

}
