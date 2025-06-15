package com.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class PointToPoint {

    static class Sender extends AbstractVerticle {
        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.setPeriodic(1000, id -> {
                vertx.eventBus().send("point.to.point.address", "Message-1");
            });
        }
    }

    static class Receiver extends AbstractVerticle {
        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<String>consumer("point.to.point.address", message -> {
                System.out.println("Received message: " + message.body());
            });
        }
    }

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new Sender());
        vertx.deployVerticle(new Receiver());
    }
}
