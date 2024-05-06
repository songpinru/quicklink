package com.demo.core;

import java.util.Arrays;
 import com.demo.di.Application;

import static com.demo.di.Application.ARGS;

public class Main {

    public static void main(String[] args) {

        String[] instance = new Application(args).initialize().entrypoint(String[].class, ARGS).getEntryPoint();
        System.out.println(Arrays.toString(instance));
    }


}