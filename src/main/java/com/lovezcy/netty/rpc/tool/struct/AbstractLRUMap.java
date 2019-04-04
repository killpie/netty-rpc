package com.lovezcy.netty.rpc.tool.struct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dingzhaolei
 * @date 2019/2/26 18:08
 **/
public abstract class AbstractLRUMap<K,V> implements Map<K,V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLRUMap.class);
    private final DoubleLinkedList<LRUElementDescriptor<K,V>> list;
    private Map<K,LRUElementDescriptor<K,V>> map;

    int hitCnt = 0;
    int missCnt = 0;
    int putCnt = 0;

    private int chunkSize = 0;

    private final Lock lock = new ReentrantLock();

    public AbstractLRUMap(){
        list = new DoubleLinkedList<>();
        map = new ConcurrentHashMap<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        V retVal = null;
        LOGGER.info("getting item  for key :{}" , key);
        LRUElementDescriptor<K,V> element = map.get(key);
        if (element != null){
            hitCnt++;
            retVal = element.getPayload();
            list.makeFirst(element);
        }else {
            missCnt++;
        }
        LOGGER.info("getting item  for key :{},value:{}" , key, retVal);
        return retVal;
    }

    @Override
    public V put(K key, V value) {
        putCnt++;
        LRUElementDescriptor<K,V> oldVal = null;
        try {
            lock.lock();
            addFirst(key,value);
            LRUElementDescriptor<K,V> node = list.getFirst();
            oldVal = map.put(node.getKey(),node);
            if (oldVal != null && node.getKey().equals(oldVal.getKey())){
                list.remove(oldVal);
            }
        }finally {
            lock.unlock();
        }

        if (shouldRemove()){
            LOGGER.info("容量到达上限，开始移除数据");
            while (shouldRemove()){

                try {
                    lock.lock();
                    LRUElementDescriptor<K,V> last = list.getLast();

                    if (last == null){
                        LOGGER.error("链表为空");
                        throw new Error("链表为空");
                    }
                    if (last != null && map.get(last.getKey()) == null){
                        LOGGER.error("remove key fail key:{},value:{}",last.getKey(),last.getPayload());
                    }
                    list.removeLast();
                }catch (Exception e){
                    LOGGER.error("update :{}",e);
                }finally {
                    lock.unlock();
                }
            }
        }

        if (oldVal!=null){
            return oldVal.getPayload();
        }
        return null;
    }

    protected abstract boolean shouldRemove();

    private void addFirst(K key, V value){

        try {
            lock.lock();
            LRUElementDescriptor<K,V> node = new LRUElementDescriptor<>(key,value);
            list.addFirst(node);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        LOGGER.info("remove key:{}",key);
        try {
            lock.lock();
            LRUElementDescriptor<K,V> node = map.remove(key);
            if (node != null){
                list.remove(node);
                LOGGER.info("remove key:{},value:{}",key,node.getPayload());
                return node.getPayload();
            }else {
                LOGGER.info("未命中节点");
            }
        }finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> source) {
        if (source == null){
            return;
        }
        for (Map.Entry<? extends K, ? extends V> entry:source.entrySet()
             ) {
            put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            list.removeAll();
            map.clear();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public Set keySet() {
        return null;
    }

    @Override
    public Collection values() {

        Collection<LRUElementDescriptor<K,V>> list = map.values();

        List values = new ArrayList();

        for (LRUElementDescriptor<K,V> node:list
             ) {
            values.add(node.getPayload());
        }
        return values;
    }

    @Override
    public Set<Entry<K,V>> entrySet() {

        try {
            lock.lock();
            Set<Entry<K,LRUElementDescriptor<K,V>>> source = map.entrySet();
            Set<Entry<K,V>> unWrapped = new HashSet<>();

            for (Entry<K,LRUElementDescriptor<K,V>> entry:source
                 ) {
                Entry<K,V> target = new LRUMapEntry<>(entry.getKey(),entry.getValue().getPayload());
                unWrapped.add(target);
            }

            return unWrapped;
        }finally {
            lock.unlock();
        }
    }
}
