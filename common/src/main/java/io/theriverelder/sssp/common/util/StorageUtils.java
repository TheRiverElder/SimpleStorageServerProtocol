package io.theriverelder.sssp.common.util;

import io.theriverelder.sssp.common.model.ChildInformationCache;
import io.theriverelder.sssp.common.model.DirectoryInformationCache;
import io.theriverelder.sssp.common.model.ItemInformationCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class StorageUtils {
    public static void checkExists(File item) throws IllegalArgumentException {
        if (!item.exists()) throw new IllegalArgumentException("Path does not exist: " + item.getAbsolutePath());
    }

    public static void checkFileExists(File item) throws IllegalArgumentException {
        checkExists(item);
        if (!item.isFile()) throw new IllegalArgumentException("Path is not a file: " + item.getAbsolutePath());
    }

    public static void checkDirectoryExists(File item) throws IllegalArgumentException {
        checkExists(item);
        if (!item.isDirectory()) throw new IllegalArgumentException("Path is not a directory: " + item.getAbsolutePath());
    }

    public static DirectoryInformationCache readChildrenInformation(File directory) throws IllegalArgumentException {
        File[] subItems;
        if ("".equals(directory.getPath())) {
            subItems = File.listRoots();
        } else {
            checkDirectoryExists(directory);
            subItems = directory.listFiles();
        }
        return new DirectoryInformationCache(directory, Arrays.stream(Objects.requireNonNull(subItems))
                .map(subItem -> new ChildInformationCache(subItem, subItem.isFile(), subItem.isDirectory()))
                .collect(Collectors.toList()));
    }

    public static ItemInformationCache readInformation(File item) {
        if ("".equals(item.getPath())) return new ItemInformationCache(item, true, false, true, 0, -1);
        return new ItemInformationCache(
                item,
                item.exists(),
                item.isFile(),
                item.isDirectory(),
                item.length(),
                item.lastModified()
        );
    }

    public static boolean rename(File source, File target) throws IllegalArgumentException {
        checkExists(source);
        return source.renameTo(target);
    }

    public static boolean delete(File item) throws IllegalArgumentException {
        checkExists(item);
        return item.delete();
    }

    public static boolean add(File file, InputStream inputStream, long inputLength) throws IllegalArgumentException, IOException {
        if (file.exists()) {
            if (!file.isFile()) throw new IllegalArgumentException("Is not a file: " + file.getAbsolutePath());
        } else {
            File parent = file.getParentFile();
            if (parent == null) throw new IllegalArgumentException("No parent directory: " + file.getAbsolutePath());
            if (!parent.exists()) {
                if (parent.mkdirs()) throw new IllegalArgumentException("Cannot create parent directory: " + parent.getAbsolutePath());
            }
            if (!parent.isDirectory()) throw new IllegalArgumentException("Is not a directory: " + parent.getAbsolutePath());
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            int counter = 0;
            while ((inputLength >= 0 && counter < inputLength) && (counter = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                fileOutputStream.write(buffer, 0, counter);
            }
        }
        return true;
    }
}
