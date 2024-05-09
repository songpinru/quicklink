package com.prdi.core.core;

import com.prdi.core.core.test.TestService;
import com.prdi.di.Application;

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