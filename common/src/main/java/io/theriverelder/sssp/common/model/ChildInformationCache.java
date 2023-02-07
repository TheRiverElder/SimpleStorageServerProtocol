package io.theriverelder.sssp.common.model;

import com.alibaba.fastjson2.JSONObject;
import io.theriverelder.sssp.common.JsonSource;

import java.io.File;

public record ChildInformationCache(
        File item,
        boolean isFile,
        boolean idDirectory
) implements JsonSource {

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject(3);
        jsonObject.put("name", item.getName());
        jsonObject.put("isFile", isFile);
        jsonObject.put("idDirectory", idDirectory);
        return jsonObject;
    }
}
