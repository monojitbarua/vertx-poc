package com.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

public class Main extends AbstractVerticle {

    private PgPool db;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        ConfigStoreOptions yamlStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", "application.yaml"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(yamlStore);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

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

        retriever.getConfig(ar -> {
            if (ar.failed()) {
                startPromise.fail(ar.cause());
            } else {
                JsonObject config = ar.result();
                JsonObject app = config.getJsonObject("app");
                JsonObject dbConfig = config.getJsonObject("db");
                JsonObject server = config.getJsonObject("server");

                int port = server.getInteger("port");
                String appName = app.getString("name");

                System.out.println("App Name: " + appName);
                System.out.println("Starting HTTP server on port " + port);

                //db pool
                var dbConnectionOPtions = new PgConnectOptions()
                        .setHost(dbConfig.getString("host"))
                        .setUser(dbConfig.getString("user"))
                        .setPassword(dbConfig.getString("password"))
                        .setPort(Integer.parseInt(dbConfig.getString("port")))
                        .setDatabase(dbConfig.getString("name"));
                PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
                db = PgPool.pool(vertx, dbConnectionOPtions, poolOptions);

                //setup routes
                final Router router = Router.router(vertx);
                router.get("/asset").handler(ctx -> {
                    db.query("SELECT asset_id, asset_name FROM asset")
                            .execute(asyncResult -> {
                                if (asyncResult.succeeded()) {
                                    JsonArray assets = new JsonArray();
                                    asyncResult.result().forEach(row -> {
                                        assets.add(new Asset(row.getString("asset_id"), row.getString("asset_name")));
                                    });
                                    ctx.response().putHeader("content-type", "application/json").end(assets.encodePrettily());
                                } else {
                                    ctx.response().setStatusCode(500).end("DB Error: " + ar.cause().getMessage());
                                }
                            });
                });
                router.get("/").handler(ctx->{
                    ctx.response().putHeader("content-type", "text/plain").end("Hello World!");
                });

                router.get("/asset/:asset_id").handler(this::handleGetAssetById);

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
        });


    }

    private void handleGetAssetById(RoutingContext ctx) {
        String assetId = ctx.pathParam("asset_id");
        System.out.println("Query for assetId:" + assetId);

        db.preparedQuery("SELECT asset_id, asset_name FROM asset WHERE asset_id=$1")
                .execute(Tuple.of(assetId), ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().size() > 0) {
                            Row row = ar.result().iterator().next();
                            JsonObject json = new JsonObject()
                                    .put("asset_id", row.getString("asset_id"))
                                    .put("asset_name", row.getString("asset_name"));
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(json.encodePrettily());
                        } else {
                            ctx.response().setStatusCode(404).end("Asset not found");
                        }
                    } else {
                        ctx.response().setStatusCode(500).end("DB Error: " + ar.cause().getMessage());
                    }
                });
    }

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new Main());
    }
}

