package com.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class VerticleEventLoop extends AbstractVerticle {

    public static void main(String[] args) {
        var v = Vertx.vertx();
        v.deployVerticle(new VerticleEventLoop());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        System.out.println("Inside " + getClass().getName() + " verticle");
        startPromise.complete();
        Thread.sleep(5000);
    }
}