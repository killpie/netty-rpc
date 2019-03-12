package com.lovezcy.netty.rpc.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author dingzhaolei
 * @date 2019/3/7 15:51
 * 数据报格式基类
 **/
public abstract class Packet {
    //协议版本
    @Getter
    private byte version = 1;

    public abstract byte getCommand();
}
