package com.javacompare.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Utility for reading text files with charset and BOM detection.
 */
public class FileLoader {

    public record LoadedFile(String fileName, String content, Charset charset) {}

    /**
     * Reads a file from disk into a string with encoding detection.
     */
    public static LoadedFile loadFile(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file: " + (file == null ? "null" : file.getAbsolutePath()));
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        Charset charset = detectCharset(bytes);
        String text = new String(stripBOM(bytes), charset);

        return new LoadedFile(file.getName(), text, charset);
    }

    private static Charset detectCharset(byte[] bytes) {
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            return StandardCharsets.UTF_8;
        }
        if (bytes.length >= 2 && bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) {
            return StandardCharsets.UTF_16BE;
        }
        if (bytes.length >= 2 && bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) {
            return StandardCharsets.UTF_16LE;
        }
        return StandardCharsets.UTF_8;
    }

    private static byte[] stripBOM(byte[] bytes) {
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            byte[] stripped = new byte[bytes.length - 3];
            System.arraycopy(bytes, 3, stripped, 0, stripped.length);
            return stripped;
        }
        if (bytes.length >= 2 && ((bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) || (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE))) {
            byte[] stripped = new byte[bytes.length - 2];
            System.arraycopy(bytes, 2, stripped, 0, stripped.length);
            return stripped;
        }
        return bytes;
    }
}
