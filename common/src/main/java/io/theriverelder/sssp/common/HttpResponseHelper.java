package io.theriverelder.sssp.common;

import io.theriverelder.sssp.common.model.JsonResponseBody;
import io.theriverelder.sssp.common.util.StorageUtils;
import io.theriverelder.sssp.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpResponseHelper {
    

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

    

    protected static String checkAndGetParam(Map<String, String> queryParams, String key) throws Exception {
        String value = queryParams.get(key);
        if (value == null || value.isBlank()) throw new Exception("未获取到" + key + "字段");
        return value;
    }

    public static void process(ResponseSupporter supporter) throws IOException {
        URI uri = supporter.getRequestUri();

        Map<String, String> queryParams = parseUriQuery(supporter.getRequestUri().getQuery());
        
        try {
            String path = checkAndGetParam(queryParams, QUERY_NAME_PATH);
            if ("".equals(path)) {
                path = StringUtils.getPath(uri);
            }
            String action = checkAndGetParam(queryParams, QUERY_NAME_ACTION);

            switch (action) {
                case ACTION_GET -> {
                    File file = new File(path);
                    StorageUtils.checkExists(file);

//                    Files.probeContentType(file.toPath());
                    String contentType = Optional.ofNullable(URLConnection.guessContentTypeFromName(file.getName())).orElse("text/plain");
                    if (contentType.startsWith("text/")) {
                        contentType += "; charset=utf-8";
                    }

                    supporter.setResponseHeader("Content-Type", contentType);
                    setTheFuckingCorsHeaders(supporter);

                    supporter.setResponseStatus(200);
                    supporter.setResponseBodyLength(file.length());

                    try (InputStream inputStream = new FileInputStream(file)) {
                        supporter.sendResponseBody(inputStream);
                    }
//                     getLogger().info("transformation finished: {}", path);
                }
                case ACTION_GET_INFORMATION -> response(supporter, new JsonResponseBody(StorageUtils.readInformation(new File(path))));
                case ACTION_GET_CHILDREN -> response(supporter, new JsonResponseBody(StorageUtils.readChildrenInformation(new File(path))));
                case ACTION_ADD -> response(supporter, new JsonResponseBody(StorageUtils.add(new File(path), supporter.getRequestBody())));
                case ACTION_DELETE -> response(supporter, new JsonResponseBody(StorageUtils.delete(new File(path))));
                case ACTION_RECYCLE -> response(supporter, new JsonResponseBody("暂不支持操作：回收：" + path));
                case ACTION_RENAME -> response(supporter, new JsonResponseBody(StorageUtils.rename(new File(path), new File(checkAndGetParam(queryParams, QUERY_NAME_TARGET)))));
                default -> response(supporter, new JsonResponseBody("未知行为：" + action));
            }
        } catch (Exception e) {
            response(supporter, new JsonResponseBody(e.getMessage()));
        }
    }

    protected static void response(ResponseSupporter supporter, @NotNull JsonResponseBody jsonResponseBody) throws IOException {
        String responseBodyString = jsonResponseBody.toJsonObject().toJSONString();
        // getLogger().info("response: {}", responseBodyString.substring(0, Math.min(64, responseBodyString.length())));
        byte[] responseBodyBytes = responseBodyString.getBytes(StandardCharsets.UTF_8);

        supporter.setResponseHeader("Content-Type", "application/json; charset=utf-8");
        setTheFuckingCorsHeaders(supporter);

        supporter.setResponseStatus(200);
        supporter.setResponseBodyLength(responseBodyBytes.length);
        
        try (InputStream inputStream = new ByteArrayInputStream(responseBodyBytes)) {
            supporter.sendResponseBody(inputStream);
        }
    }

    public static void setTheFuckingCorsHeaders(ResponseSupporter supporter) {
        supporter.setResponseHeader("Access-Control-Allow-Origin", "*");
        supporter.setResponseHeader("Access-Control-Allow-Methods", "*");
        supporter.setResponseHeader("Access-Control-Max-Age", "3600");
        supporter.setResponseHeader("Access-Control-Allow-Headers", "*");
        supporter.setResponseHeader("Access-Control-Allow-Credentials", "true");
    }
}
