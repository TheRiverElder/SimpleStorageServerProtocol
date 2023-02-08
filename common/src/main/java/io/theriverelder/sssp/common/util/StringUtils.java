package io.theriverelder.sssp.common.util;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class StringUtils {
    public static String getPath(URI uri) {
        String raw = uri.getPath().replace("^/+", "");
        List<String> parts = Arrays.stream(raw.split("/"))
                .map(part -> URLDecoder.decode(part, StandardCharsets.UTF_8))
                .toList();
        if (parts.size() == 0) return "";
        return Path.of(parts.get(0), parts.subList(1, parts.size()).toArray(String[]::new)).toFile().getAbsolutePath();
    }
}
