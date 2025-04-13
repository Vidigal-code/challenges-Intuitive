package com.transformation.transformer;

import com.transformation.LogsMain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Transformer {

    public static List<List<String>> applyReplacements(List<List<String>> data, Map<String, String> replacements) {
        LogsMain.logWarning("[TRANSFORMER] Aplicando substituições...");
        List<List<String>> result = new ArrayList<>();

        for (List<String> row : data) {
            List<String> newRow = new ArrayList<>();
            for (String cell : row) {
                String newCell = cell;
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    newCell = newCell.replaceAll(entry.getKey(), entry.getValue());
                }
                newRow.add(newCell);
            }
            result.add(newRow);
        }

        LogsMain.logWarning("[TRANSFORMER] Substituições aplicadas com sucesso!");
        return result;
    }

    public static void saveToCSV(List<List<String>> data, String csvOutput) throws IOException {
        File csvFile = new File(csvOutput);
        csvFile.getParentFile().mkdirs();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvOutput), StandardCharsets.UTF_8)) {
            for (List<String> row : data) {
                writer.write(String.join(";", row));
                writer.newLine();
            }
        }
        LogsMain.logWarning("[TRANSFORMER] CSV salvo em: " + csvOutput);
    }

    public static void zipFile(String sourceFile, String zipFilePath) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            File fileToZip = new File(sourceFile);
            try (FileInputStream fis = new FileInputStream(fileToZip)) {
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
        }
        Files.delete(Paths.get(sourceFile));
        LogsMain.logWarning("[TRANSFORMER] ZIP criado em: " + zipFilePath);
    }
}

