package com.poc;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;


public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.createHttpServer()
                .requestHandler(req -> {
                    req.response()
                            .putHeader("content-type", "text/plain")
                            .end("Hello World!");
                })
                .listen(8888, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        System.out.println("--- Undeployed");
        super.stop(stopPromise);
    }

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }
}