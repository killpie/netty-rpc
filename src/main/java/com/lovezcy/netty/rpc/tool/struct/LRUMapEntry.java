package com.lovezcy.netty.rpc.tool.struct;

import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @author dingzhaolei
 * @date 2019/2/27 10:18
 **/
@ToString
public class LRUMapEntry<K,V> implements Map.Entry<K,V>, Serializable {
    private static final long serialVersionUID = 5773213571584798979L;

    private final K key;
    private V value;

    public LRUMapEntry(K key, V value){
        this.key = key;
        this.value = value;
    }


    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}
