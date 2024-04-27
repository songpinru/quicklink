package org.example;

import org.example.base.FlinkApplication;
import org.example.test.TestApplication;

public class Main  {
    public static void main(String[] args) {
        FlinkApplication.run(TestApplication.class,args);
    }
}