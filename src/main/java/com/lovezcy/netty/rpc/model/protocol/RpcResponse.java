package com.lovezcy.netty.rpc.model.protocol;

import com.lovezcy.netty.rpc.constant.DatagramFormatEnum;
import com.lovezcy.netty.rpc.model.Packet;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dingzhaolei
 * @date 2019/2/20 10:51
 **/
@Data
public class RpcResponse extends Packet implements Serializable {
    private static final long serialVersionUID = -3888703143177777864L;
    private String appId;
    private String token;
    private String requestId;
    private byte[] exception;
    private Throwable errorMsg;
    private Object appResponse;

    @Override
    public byte getCommand() {
        return DatagramFormatEnum.RPC_RESPONSE.getKey();
    }
}
