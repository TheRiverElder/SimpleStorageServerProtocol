package io.theriverelder.sssp.common.model;

import com.alibaba.fastjson2.JSONObject;
import io.theriverelder.sssp.common.JsonSource;

import java.io.File;

public record ItemInformationCache(
        File item,
        boolean exists,
        boolean isFile,
        boolean idDirectory,
        long length,
        long lastModifiedTime
) implements JsonSource {

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject(3);
        jsonObject.put("path", item.getAbsolutePath());
        jsonObject.put("exists", exists);
        jsonObject.put("isFile", isFile);
        jsonObject.put("idDirectory", idDirectory);
        jsonObject.put("length", length);
        jsonObject.put("lastModifiedTime", lastModifiedTime);
        return jsonObject;
    }
}
