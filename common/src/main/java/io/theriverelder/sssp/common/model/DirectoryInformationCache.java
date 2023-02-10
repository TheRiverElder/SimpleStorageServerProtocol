package io.theriverelder.sssp.common.model;

import com.alibaba.fastjson2.JSONObject;
import io.theriverelder.sssp.common.util.JsonSource;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public record DirectoryInformationCache(
        File directory,
        List<ChildInformationCache> children
) implements JsonSource {

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", directory.getAbsolutePath());
        jsonObject.put("children", children.stream().map(JsonSource::toJsonObject).collect(Collectors.toList()));
        return jsonObject;
    }
}
