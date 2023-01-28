package io.theriverelder.sssp;

import io.theriverelder.sssp.model.ChildInformationCache;
import io.theriverelder.sssp.model.DirectoryInformationCache;
import io.theriverelder.sssp.model.ItemInformationCache;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public class Storages {

    public static void checkExists(File item) throws IllegalArgumentException {
        if (!item.exists()) throw new IllegalArgumentException(String.format("%s does not exist", item.getAbsolutePath()));
    }

    public static void checkDirectoryExists(File item) throws IllegalArgumentException {
        checkExists(item);
        if (!item.isDirectory()) throw new IllegalArgumentException(String.format("%s is not a directory", item.getAbsolutePath()));
    }

    public static String[] readChildNames(File directory) throws IllegalArgumentException {
        checkDirectoryExists(directory);
        return directory.list();
    }

    public static DirectoryInformationCache readChildrenInformation(File directory) throws IllegalArgumentException {
        checkDirectoryExists(directory);
        File[] subItems = directory.listFiles();
        return new DirectoryInformationCache(directory, Arrays.stream(Objects.requireNonNull(subItems))
                .map(subItem -> new ChildInformationCache(subItem, subItem.isFile(), subItem.isDirectory()))
                .toList()
        );
    }

    public static ItemInformationCache readInformation(File item) {
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
        File parent = file.getParentFile();
        if (!parent.exists()) {
            if (parent.mkdirs()) return false;
        } else if (!parent.isDirectory()) throw new IllegalArgumentException("Is not a directory: " + file.getAbsolutePath());

        try (inputStream; FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            inputStream.transferTo(fileOutputStream);
        }
        return true;
    }


}
