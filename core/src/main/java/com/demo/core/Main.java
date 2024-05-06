package com.demo.core;

import java.util.Arrays;

import com.demo.core.test.TestService;
import com.demo.di.Application;

import static com.demo.di.Application.ARGS;

public class Main {

    public static void main(String[] args) {

        TestService entryPoint = Application
                .app(args)
                .initialize()
                .entrypoint(TestService.class)
                .getEntryPoint();
        entryPoint.run();
    }


}