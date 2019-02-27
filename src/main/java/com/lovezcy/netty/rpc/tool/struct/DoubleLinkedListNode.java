package com.lovezcy.netty.rpc.tool.struct;

import lombok.ToString;

import java.io.Serializable;

/**
 * 双向链表节点
 * @author dingzhaolei
 * @date 2019/2/26 14:47
 **/
@ToString
public class DoubleLinkedListNode<T> implements Serializable {
    private static final long serialVersionUID = 7567539069541090888L;

    private final T payload;

    public DoubleLinkedListNode<T> prev;
    public DoubleLinkedListNode<T> next;
    public DoubleLinkedListNode(T payloadP) {
        payload = payloadP;
    }

    public T getPayload(){
        return payload;
    }
}
