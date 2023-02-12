package io.theriverelder.sssp.common.model;

import com.alibaba.fastjson2.JSONObject;
import io.theriverelder.sssp.common.util.JsonSource;

import java.io.File;
import java.util.Objects;

public final class ChildInformationCache implements JsonSource {

    private final File item;
    private final boolean isFile;
    private final boolean idDirectory;

    public ChildInformationCache(
            File item,
            boolean isFile,
            boolean idDirectory
    ) {
        this.item = item;
        this.isFile = isFile;
        this.idDirectory = idDirectory;
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject(3);
        jsonObject.put("name", item.getName());
        jsonObject.put("isFile", isFile);
        jsonObject.put("idDirectory", idDirectory);
        return jsonObject;
    }

    public File item() {
        return item;
    }

    public boolean isFile() {
        return isFile;
    }

    public boolean idDirectory() {
        return idDirectory;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ChildInformationCache that = (ChildInformationCache) obj;
        return Objects.equals(this.item, that.item) &&
                this.isFile == that.isFile &&
                this.idDirectory == that.idDirectory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, isFile, idDirectory);
    }

    @Override
    public String toString() {
        return "ChildInformationCache[" +
                "item=" + item + ", " +
                "isFile=" + isFile + ", " +
                "idDirectory=" + idDirectory + ']';
    }

}
