package com.lovezcy.netty.rpc.demo.server.impl;

import com.lovezcy.netty.rpc.demo.server.RaceTestService;

public class RaceTestServiceImpl implements RaceTestService {
    public int add(int i, int j){
        return i+j;
    }
}
