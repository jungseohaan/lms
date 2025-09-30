package com.visang.aidt.lms.api.utility.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileTypeValidator {

    private static final Map<String, Predicate<byte[]>> MAGIC_NUMBERS = new HashMap<>();
    static {
        MAGIC_NUMBERS.put("jpg", h -> h[0] == (byte)0xFF && h[1] == (byte)0xD8);
        MAGIC_NUMBERS.put("jpeg", MAGIC_NUMBERS.get("jpg"));
        MAGIC_NUMBERS.put("png", h -> h[0] == (byte)0x89 && h[1] == 0x50 && h[2] == 0x4E && h[3] == 0x47);
        MAGIC_NUMBERS.put("gif", h -> h[0] == 0x47 && h[1] == 0x49 && h[2] == 0x46);
        MAGIC_NUMBERS.put("pdf", h -> h[0] == 0x25 && h[1] == 0x50 && h[2] == 0x44 && h[3] == 0x46);
        MAGIC_NUMBERS.put("zip", h -> h[0] == 0x50 && h[1] == 0x4B);
        MAGIC_NUMBERS.put("hwp", h -> h[0] == (byte)0xD0 && h[1] == (byte)0xCF && h[2] == 0x11 && h[3] == (byte)0xE0);
        MAGIC_NUMBERS.put("doc", h -> h[0] == (byte)0xD0 && h[1] == (byte)0xCF && h[2] == 0x11 && h[3] == (byte)0xE0);
        MAGIC_NUMBERS.put("txt", h -> true);
    }

    private static final List<String> DANGEROUS_PATTERNS = Arrays.asList(
            "(?i)<script", "(?i)<iframe", "(?i)<object", "(?i)<embed", "(?i)javascript:"
    );

    public static boolean isAllowedFile(File file) throws IOException {
        String extension = FileUtil.getFileExtension(file.getName()).toLowerCase().trim();

        if (FileUtil.getAllowedExtensions().contains(extension)) return false;

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = fis.read(header);
            if (read < 4) return false;
            Predicate<byte[]> validator = MAGIC_NUMBERS.get(extension);
            if (validator == null || !validator.test(header)) return false;
        }

        // TXT 내용 검사
        if (extension.equals("txt")) {
            if (containsDangerousContent(file)) return false;
        }
        // ZIP 내부 파일 검사
        if (extension.equals("zip")) {
            if (ZipInspector.containsBlockedEntries(file)) return false;
        }
        return true;
    }

    private static boolean containsDangerousContent(File file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                for (String pattern : DANGEROUS_PATTERNS) {
                    if (line.toLowerCase().matches(".*" + pattern + ".*")) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    // ZIP 내부 검사
    public static class ZipInspector {
        private static final List<String> BLOCKED_EXTENSIONS = Arrays.asList("exe", "jsp", "js", "bat", "cmd", "sh");

        public static boolean containsBlockedEntries(File zipFile) throws IOException {
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    String ext = getFileExtension(entry.getName()).toLowerCase();
                    if (BLOCKED_EXTENSIONS.contains(ext)) return true;
                }
            }
            return false;
        }

        private static String getFileExtension(String fileName) {
            int lastDot = fileName.lastIndexOf('.');
            return lastDot == -1 ? "" : fileName.substring(lastDot + 1);
        }
    }
}
