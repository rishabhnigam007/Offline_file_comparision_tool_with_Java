package com.javacompare.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should read standard UTF-8 text file")
    void shouldReadUtf8File() throws IOException {
        Path file = tempDir.resolve("sample.java");
        Files.writeString(file, "public class Sample {\n    int a = 10;\n}");

        FileLoader.LoadedFile loaded = FileLoader.loadFile(file.toFile());

        assertThat(loaded.fileName()).isEqualTo("sample.java");
        assertThat(loaded.content()).contains("public class Sample");
        assertThat(loaded.charset()).isEqualTo(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Should strip UTF-8 BOM if present")
    void shouldStripUtf8Bom() throws IOException {
        Path file = tempDir.resolve("bom_sample.txt");
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] text = "Hello BOM".getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[bom.length + text.length];
        System.arraycopy(bom, 0, combined, 0, bom.length);
        System.arraycopy(text, 0, combined, bom.length, text.length);

        Files.write(file, combined);

        FileLoader.LoadedFile loaded = FileLoader.loadFile(file.toFile());
        assertThat(loaded.content()).isEqualTo("Hello BOM");
    }
}
