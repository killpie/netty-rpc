package com.lovezcy.netty.rpc.tool.struct;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * LRU的MAP最近最常用的保留
 * @author dingzhaolei
 * @date 2019/2/27 10:30
 **/
public class LRUMap<K,V> extends AbstractLRUMap<K,V>{

    int maxObjects = -1;
    AtomicInteger counter = new AtomicInteger(0);

    public LRUMap(){
        super();
    }

    public LRUMap(int maxObjects){
        super();
        this.maxObjects = maxObjects;
    }

    @Override
    protected boolean shouldRemove() {
        return maxObjects>0&&this.size()>maxObjects;
    }
}
