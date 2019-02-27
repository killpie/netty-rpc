package com.lovezcy.netty.rpc.tool.struct;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * synchronized 锁住当前线程操作对像 this
 * @author dingzhaolei
 * @date 2019/2/26 14:55
 **/
@ToString
public class DoubleLinkedList<T extends DoubleLinkedListNode> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleLinkedList.class);

    private int size;
    private T first;
    private T last;

    public DoubleLinkedList(){
        super();
    }

    public synchronized void addLast(T me){
        //如果是空链表
        if (first == null){
            first = me;
        }else {
            last.next = me;
            me.prev = last;
        }
        //更改尾指针
        last = me;
        size++;
    }

    public synchronized void addFirst(T me){
        if (last == null){
            last = me;
        }else {
            first.prev = me;
            me.next = first;
        }
        //更改头指针
        first = me;
        size++;
    }

    public synchronized T getFirst(){
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("get first node");
        }

        return first;
    }

    public synchronized T getLast(){
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("get last node");
        }
        return last;
    }

    /**
     * 将<param>ln<param/> 调整为头节点
     * @param ln
     */
    public synchronized void makeFirst(T ln){
        //ln已经是头结点
        if (ln == first){
            return;
        }

        //ln 不为尾节点
        if (ln.next != null){
            ln.next.prev = ln.prev;
        }
        ln.prev.next = ln.next;
        first.prev = ln;
        ln.next = first;
        ln.prev = null;
        //更新头指针
        first = ln;
    }

    /**
     * 将<param>ln<param/> 调整为尾节点
     * @param ln
     */
    public synchronized void makeLast(T ln){
        //ln已经是尾节点
        if (ln == last){
            return;
        }

        //ln 不是头结点
        if (ln.prev != null){
            ln.next.prev = ln.prev;
            ln.prev.next = ln.next;
        }
        ln.prev = last;
        ln.next = null;
        last.next = ln;
    }

    public synchronized void removeAll(){
        for (T me = first; first != null; ){
            if (me.prev != null){
                me.prev = null;
            }

            me = (T) me.next;
        }

        first = last = null;
        size = 0;
    }

    public synchronized boolean remove(T me){
        if (me == null){
            throw new RuntimeException("删除的节点不能为空");
        }
        //如果只有一个节点
        if (me.prev == null && me.next == null){
            first = last = null;
            size = 0;
            return true;
        }

        //该节点为头节点
        if (me.prev == null){
            first = (T) me.next;
            me.next = null;
            me = null;
            size--;
            return true;
        }

        //该节点为尾节点
        if (me.next == null){
            last = (T) me.prev;
            me.prev = null;
            me = null;
            size--;
            return true;
        }

        me.prev.next = me.next;
        me.next.prev = me.prev;
        me = null;
        size--;
        return true;
    }

    public synchronized T removeLast(){
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("remove last node");
        }
        T temp = last;
        remove(last);

        return temp;
    }


    public synchronized int size(){
        return size;
    }



}
