package com.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle1 extends AbstractVerticle{

    public static void main(String[] args) {
        System.out.println("IN main method");
        Vertx v = Vertx.vertx();
        v.deployVerticle(new MainVerticle1());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.deployVerticle(new VerticleA());
        vertx.deployVerticle(new VerticleB());
        vertx.deployVerticle(VerticleN.class.getName(), new DeploymentOptions().setInstances(2));
        vertx.deployVerticle(VerticleConfig.class.getName(), new DeploymentOptions().setConfig(new JsonObject().put("k1","v1").put("k2","v2")));
    }
}
