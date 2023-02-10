package io.theriverelder.sssp.common.model;

import com.alibaba.fastjson2.JSONObject;
import io.theriverelder.sssp.common.util.JsonSource;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DirectoryInformationCache implements JsonSource {

    private final File directory;
    private final List<ChildInformationCache> children;

    public DirectoryInformationCache(
            File directory,
            List<ChildInformationCache> children
    ) {
        this.directory = directory;
        this.children = children;
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", directory.getAbsolutePath());
        jsonObject.put("children", children.stream().map(JsonSource::toJsonObject).collect(Collectors.toList()));
        return jsonObject;
    }

    public File directory() {
        return directory;
    }

    public List<ChildInformationCache> children() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DirectoryInformationCache) obj;
        return Objects.equals(this.directory, that.directory) &&
                Objects.equals(this.children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directory, children);
    }

    @Override
    public String toString() {
        return "DirectoryInformationCache[" +
                "directory=" + directory + ", " +
                "children=" + children + ']';
    }

}
