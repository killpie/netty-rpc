package com.lovezcy.netty.rpc.demo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ConsumerTestLog {

    public static OutputStream getPerformanceOutputStream() throws FileNotFoundException {
        File file = new File("performance.log");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        return fileOutputStream;
    }
}
