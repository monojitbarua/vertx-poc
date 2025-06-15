package com.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.ServerWebSocket;

public class SimpleWSVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.createHttpServer()
                .webSocketHandler(new SimpleWSHandler())
                .listen(8900, httpServerAsyncResult -> {
                    if (httpServerAsyncResult.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(httpServerAsyncResult.cause());
                    }
                });
    }

    class SimpleWSHandler implements Handler<ServerWebSocket> {
        @Override
        public void handle(ServerWebSocket ws) {

            if (!"/path1".equalsIgnoreCase(ws.path())) {
                ws.writeFinalTextFrame(String.format("Wrong path. Only path1 is accepted, Your path: [%s]" , ws.path()));
                ws.close((short) 1000, "closure due to wrong path");
                return;
            }

            System.out.println(String.format("Opening WS connection path: [%s], id: [%s]", ws.path(), ws.textHandlerID()));
            ws.accept();
            ws.frameHandler(frame -> {
                System.out.println(String.format("Received: %s", frame.textData()));
            });
            ws.endHandler(onClose -> System.out.println(String.format("Closed: %s", ws.textHandlerID())));
            ws.exceptionHandler(err -> System.out.println(String.format("Error: %s", ws.textHandlerID())));
            ws.writeTextMessage("Connected, hello world.");
        }
    }

}
