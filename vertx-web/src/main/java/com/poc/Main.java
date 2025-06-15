package com.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;

public class Main extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        final Router router = Router.router(vertx);
        router.get("/asset").handler(ctx -> {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add("asset1");
            jsonArray.add("asset2");
            jsonArray.add("asset3");
            ctx.response().end(jsonArray.toBuffer());
        });

        /*vertx.createHttpServer().requestHandler(req -> {
            req.response().putHeader("content-type", "text/plain").end("Hello World!");
        }).exceptionHandler(ex -> System.out.println("Exception occurred in handler: " + ex.getMessage())).listen(8888, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8888");
            } else {
                startPromise.fail(http.cause());
                System.out.println("Could not start HTTP server");
            }
        });*/

        vertx.createHttpServer()
                .requestHandler(router)
                .exceptionHandler(
                        ex -> System.out.println("Exception occurred in handler: " + ex.getMessage()))
                .listen(8888, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("HTTP server started on port 8888");
                    } else {
                        startPromise.fail(http.cause());
                        System.out.println("Could not start HTTP server");
                    }
                });
    }

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new Main());
    }
}