package com.lovezcy.netty.rpc.tool.struct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author dingzhaolei
 * @date 2019/2/26 17:30
 **/
@ToString
public class LRUElementDescriptor<K,V> extends DoubleLinkedListNode<V>{

    private static final long serialVersionUID = 4875035473978990124L;

    @Setter
    @Getter
    private K key;
    public LRUElementDescriptor(K key, V value){
        super(value);
        this.setKey(key);
    }
}
