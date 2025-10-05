package com.poc.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class VerticleWorker extends AbstractVerticle {

    public static void main(String[] args) {
        var v = Vertx.vertx();
        v.deployVerticle(new VerticleWorker());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        startPromise.complete();
        vertx.executeBlocking(
                event -> {
                    System.out.println("executing custom blocking operation");
                    try {
                        Thread.sleep(5000);
                        event.complete();
                    } catch (InterruptedException e) {
                        event.fail(e);
                    }
                }, result -> {
                    if (result.succeeded()) {
                        System.out.println("custom blocking is completed");
                    } else {
                        System.out.println("custom blocking call is failed " + result.cause());
                    }
                }
        );
    }
}
