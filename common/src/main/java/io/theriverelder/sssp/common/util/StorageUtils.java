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
        if (directory.getPath().isBlank()) {
            subItems = File.listRoots();
        } else {
            checkDirectoryExists(directory);
            subItems = directory.listFiles();
        }
        return new DirectoryInformationCache(directory, Arrays.stream(Objects.requireNonNull(subItems))
                .map(subItem -> new ChildInformationCache(subItem, subItem.isFile(), subItem.isDirectory()))
                .toList()
        );
    }

    public static ItemInformationCache readInformation(File item) {
        if (item.getPath().isBlank()) return new ItemInformationCache(item, true, false, true, 0, -1);
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

    public static boolean add(File file, InputStream inputStream) throws IllegalArgumentException, IOException {
        if (file.exists()) {
            if (!file.isFile()) throw new IllegalArgumentException("Is not a file: " + file.getAbsolutePath());
        } else {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                if (parent.mkdirs()) throw new IllegalArgumentException("Cannot create parent directory: " + parent.getAbsolutePath());
            }
            if (!parent.isDirectory()) throw new IllegalArgumentException("Is not a directory: " + parent.getAbsolutePath());
        }

        try (inputStream; FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            inputStream.transferTo(fileOutputStream);
        }
        return true;
    }
}