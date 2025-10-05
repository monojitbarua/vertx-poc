package com.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class VerticleN extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        System.out.println("Inside "+ getClass().getName() + " start method" + " thread name: " + Thread.currentThread().getName());
    }
}