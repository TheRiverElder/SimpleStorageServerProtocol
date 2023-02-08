package io.theriverelder.sssp.desktop.restful;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.theriverelder.sssp.common.model.JsonResponseBody;
import io.theriverelder.sssp.desktop.DesktopSimpleStorageServer;
import io.theriverelder.sssp.desktop.Storages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class InformationQueryHandler implements HttpHandler {

    private final DesktopSimpleStorageServer server;

    public InformationQueryHandler(DesktopSimpleStorageServer server) {
        this.server = server;
    }

    public DesktopSimpleStorageServer getServer() {
        return server;
    }

    @Nullable
    public Logger getLogger() {
        return server.getLogger();
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        getLogger().info("Connection in：{}", exchange.getRequestURI());

        exchange.getRequestMethod();

        Map<String, String> queryParams = parseUriQuery(exchange.getRequestURI().getQuery());


        try {
            String path = checkAndGetParam(queryParams, QUERY_NAME_PATH);
            String action = checkAndGetParam(queryParams, QUERY_NAME_ACTION);
            getLogger().info("action={}, path={}", action, path);

            switch (action) {
                case ACTION_GET -> {
                    File file = new File(path);
                    Storages.checkExists(file);

                    Headers responseHeaders = exchange.getResponseHeaders();
                    responseHeaders.set("Content-Type", "plain/text; charset=utf-8");
                    setTheFuckingCorsHeaders(responseHeaders);

                    exchange.sendResponseHeaders(200, file.length());
                    try (
                            InputStream inputStream = new FileInputStream(file);
                            OutputStream outputStream = exchange.getResponseBody()
                    ) {
                        inputStream.transferTo(outputStream);
                    }
                    getLogger().info("transformation finished: {}", path);
                }
                case ACTION_GET_INFORMATION -> response(exchange, new JsonResponseBody(Storages.readInformation(new File(path))));
                case ACTION_GET_CHILDREN -> response(exchange, new JsonResponseBody(Storages.readChildrenInformation(new File(path))));
                case ACTION_ADD -> response(exchange, new JsonResponseBody(Storages.add(new File(path), exchange.getRequestBody())));
                case ACTION_DELETE -> response(exchange, new JsonResponseBody(Storages.delete(new File(path))));
                case ACTION_RECYCLE -> response(exchange, new JsonResponseBody("暂不支持操作：回收：" + path));
                case ACTION_RENAME -> response(exchange, new JsonResponseBody(Storages.rename(new File(path), new File(checkAndGetParam(queryParams, QUERY_NAME_TARGET)))));
                default -> response(exchange, new JsonResponseBody("未知行为：" + action));
            }
        } catch (Exception e) {
            response(exchange, new JsonResponseBody(e.getMessage()));
        }

        exchange.close();
    }

    protected void response(HttpExchange exchange, @NotNull JsonResponseBody jsonResponseBody) throws IOException {
        String responseBodyString = jsonResponseBody.toJsonObject().toJSONString();
        getLogger().info("response: {}", responseBodyString.substring(0, Math.min(64, responseBodyString.length())));
        byte[] responseBodyBytes = responseBodyString.getBytes(StandardCharsets.UTF_8);

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "application/json; charset=utf-8");
        setTheFuckingCorsHeaders(responseHeaders);

        exchange.sendResponseHeaders(200, responseBodyBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBodyBytes);
        }
    }

    public static void setTheFuckingCorsHeaders(Headers headers) {
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "*");
        headers.set("Access-Control-Max-Age", "3600");
        headers.set("Access-Control-Allow-Headers", "*");
        headers.set("Access-Control-Allow-Credentials", "true");
    }

}
