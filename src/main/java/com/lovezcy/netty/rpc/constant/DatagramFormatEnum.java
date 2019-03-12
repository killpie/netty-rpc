package com.lovezcy.netty.rpc.constant;

import lombok.Getter;

/**
 * 数据报格式
 * @author dingzhaolei
 * @date 2019/3/11 14:48
 **/
public enum DatagramFormatEnum {
    RPC_RESPONSE((byte)1,"rpc响应"),
    RPC_REQUEST((byte)2,"rpc请求")
    ;


    @Getter
    private byte key;
    @Getter
    private String value;
    private DatagramFormatEnum(byte key, String value){
        this.key = key;
        this.value = value;
    }
}
