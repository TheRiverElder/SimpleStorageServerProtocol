package io.theriverelder.sssp.model;

import com.alibaba.fastjson2.JSONObject;
import io.theriverelder.sssp.JsonSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonResponseBody implements JsonSource {

    private final boolean succeeded;
    @Nullable
    private final String errorMessage;
    @Nullable
    private final JsonSource data;

    public JsonResponseBody(boolean succeeded, @Nullable String errorMessage, @Nullable JsonSource data) {
        this.succeeded = succeeded;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public JsonResponseBody(boolean succeeded) {
        this(succeeded, null, null);
    }

    public JsonResponseBody(@NotNull String errorMessage) {
        this(false, errorMessage, null);
    }

    public JsonResponseBody(@NotNull JsonSource data) {
        this(true, null, data);
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject(3);
        jsonObject.put("succeeded", succeeded);
        if (errorMessage != null) {
            jsonObject.put("errorMessage", errorMessage);
        }
        if (data != null) {
            jsonObject.put("data", data.toJsonObject());
        }
        return jsonObject;
    }
}
