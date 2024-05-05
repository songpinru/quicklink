package com.demo.core;

import java.util.Arrays;
 import com.demo.di.Application;
import static com.demo.di.Application.ARGS;

public class Main {

    public static void main(String[] args) {

        System.out.println(Arrays.toString(Application
                .init(args)
                .getInstance(String[].class, ARGS)));
    }


}