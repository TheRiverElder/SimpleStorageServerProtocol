package io.theriverelder.sssp.restful;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.theriverelder.sssp.SimpleStorageServer;
import io.theriverelder.sssp.Storages;
import io.theriverelder.sssp.model.JsonResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.theriverelder.sssp.Storages.checkExists;

public class InformationQueryHandler implements HttpHandler {

    private final SimpleStorageServer server;

    public InformationQueryHandler(SimpleStorageServer server) {
        this.server = server;
    }

    public SimpleStorageServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return server.getLogger();
    }

    public static Map<String, String> parseUriQuery(@Nullable String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isBlank()) return result;

        for (String pairString : query.split("&")) {
            String string = pairString.strip();
            if (string.length() <= 0) continue;
            int index = string.indexOf('=');
            if (index < 0) {
                result.put(string, "");
            } else {
                String key = string.substring(0, index);
                String value = string.substring(index + 1);
                result.put(key, value);
            }
        }

        return result;
    }

    public static final String QUERY_NAME_PATH = "path";
    public static final String QUERY_NAME_TARGET = "target";
    public static final String QUERY_NAME_ACTION = "action";

    // 所有行为都会返回该格式的json { succeeded: boolean; errorMessage?: string, data?: Data } 其中data字段根据action不同而不同

    /**
     * 获取信息
     * {
     * path: string;
     * exists: boolean;
     * isFile: boolean;
     * isDirectory: boolean;
     * }
     */
    public static final String ACTION_GET_INFORMATION = "get-information";

    /**
     * 获取文件夹的下级项目列表
     * {
     * children: Array<
     * name：string;
     * isFile: boolean;
     * isDirectory: boolean;
     * >;
     * }
     */
    public static final String ACTION_GET_CHILDREN = "get-children";
    /**
     * 载入文件
     */
    public static final String ACTION_GET = "get";
    /**
     * 上传新文件
     */
    public static final String ACTION_ADD = "add";
    /**
     * 删除文件，直接删除，不进入回收站
     */
    public static final String ACTION_DELETE = "delete";
    /**
     * 将文件放入回收站（如果支持）
     */
    public static final String ACTION_RECYCLE = "recycle";
    /**
     * 重命名，移动也用这个
     */
    public static final String ACTION_RENAME = "rename";


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
                    checkExists(file);

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

    protected static String checkAndGetParam(Map<String, String> queryParams, String key) throws Exception {
        String value = queryParams.get(key);
        if (value == null || value.isBlank()) throw new Exception("未获取到" + key + "字段");
        return value;
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
