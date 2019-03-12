package com.lovezcy.netty.rpc.netty.codec;


import com.lovezcy.netty.rpc.constant.DatagramFormatEnum;
import com.lovezcy.netty.rpc.model.Packet;
import com.lovezcy.netty.rpc.model.protocol.RpcRequest;
import com.lovezcy.netty.rpc.model.protocol.RpcResponse;
import com.lovezcy.netty.rpc.tool.Tool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编码解码器
 * @author dingzhaolei
 * @date 2019/3/5 16:13
 **/
@Slf4j
public class RpcCodec extends MessageToMessageCodec<ByteBuf, Packet> {

    public static final int MAGIC_NUMBER = 0x123456;
    //int 类型所占字节长度
    public static final int INTEGER_LENGTH = 4;
    public static final int BYTE_LENGTH = 1;
    private static final Map<Byte, Class<? extends Packet>> packetTypeMap;

    static {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(DatagramFormatEnum.RPC_REQUEST.getKey(), RpcRequest.class);
        packetTypeMap.put(DatagramFormatEnum.RPC_RESPONSE.getKey(), RpcResponse.class);
    }
    public RpcCodec(){
    }


    @Override
    protected  void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out)
            throws Exception{
        log.info("ctx:{},packet:{},out:{}",ctx.channel().id());
        ByteBuf byteBuf = ctx.alloc().ioBuffer();
        byte[] body = Tool.serialize(packet);

        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(body.length);
        byteBuf.writeBytes(body);
        out.add(byteBuf);
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out)
            throws Exception{
        byteBuf.skipBytes(INTEGER_LENGTH);
        byte version = byteBuf.readByte();
        byte command = byteBuf.readByte();

        int bodyLen = byteBuf.readInt();
        byte[] bodyBytes = new byte[bodyLen];
        byteBuf.readBytes(bodyBytes);
        Class clazz = packetTypeMap.get(command);
        Object o = Tool.deserialize(bodyBytes,clazz);
        out.add(o);
    }

}
