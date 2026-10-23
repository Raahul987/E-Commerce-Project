package com.helpers;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtil {
    public static final String DATA_DIR = "data";

    public static void ensureFile(String fileName) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Path path = Paths.get(DATA_DIR, fileName);
            if (!Files.exists(path)) Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize data file: " + fileName, e);
        }
    }

    public static List<String> read(String fileName) {
        ensureFile(fileName);
        try {
            return Files.readAllLines(Paths.get(DATA_DIR, fileName));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read " + fileName, e);
        }
    }

    public static void write(String fileName, List<String> lines) {
        ensureFile(fileName);
        try {
            Files.write(Paths.get(DATA_DIR, fileName), lines);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write " + fileName, e);
        }
    }

    public static void append(String fileName, String line) {
        ensureFile(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(DATA_DIR, fileName),
                StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Unable to append " + fileName, e);
        }
    }

    public static String clean(String value) {
        if (value == null) return "";
        return value.replace("|", "/").replace("\n", " ").replace("\r", " ");
    }
}
