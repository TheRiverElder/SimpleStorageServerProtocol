package io.theriverelder.sssp.common.model;

import com.alibaba.fastjson2.JSONObject;
import io.theriverelder.sssp.common.util.JsonSource;

import java.io.File;
import java.util.Objects;

public final class ItemInformationCache implements JsonSource {

    private final File item;
    private final boolean exists;
    private final boolean isFile;
    private final boolean idDirectory;
    private final long length;
    private final long lastModifiedTime;

    public ItemInformationCache(
            File item,
            boolean exists,
            boolean isFile,
            boolean idDirectory,
            long length,
            long lastModifiedTime
    ) {
        this.item = item;
        this.exists = exists;
        this.isFile = isFile;
        this.idDirectory = idDirectory;
        this.length = length;
        this.lastModifiedTime = lastModifiedTime;
    }

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

    public File item() {
        return item;
    }

    public boolean exists() {
        return exists;
    }

    public boolean isFile() {
        return isFile;
    }

    public boolean idDirectory() {
        return idDirectory;
    }

    public long length() {
        return length;
    }

    public long lastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ItemInformationCache) obj;
        return Objects.equals(this.item, that.item) &&
                this.exists == that.exists &&
                this.isFile == that.isFile &&
                this.idDirectory == that.idDirectory &&
                this.length == that.length &&
                this.lastModifiedTime == that.lastModifiedTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, exists, isFile, idDirectory, length, lastModifiedTime);
    }

    @Override
    public String toString() {
        return "ItemInformationCache[" +
                "item=" + item + ", " +
                "exists=" + exists + ", " +
                "isFile=" + isFile + ", " +
                "idDirectory=" + idDirectory + ", " +
                "length=" + length + ", " +
                "lastModifiedTime=" + lastModifiedTime + ']';
    }

}
