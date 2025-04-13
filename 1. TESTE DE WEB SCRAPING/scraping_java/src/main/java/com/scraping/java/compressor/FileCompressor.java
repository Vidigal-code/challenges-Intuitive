package com.scraping.java.compressor;

import com.scraping.java.LogsMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileCompressor {
    public static void compressFiles(List<String> files, String outputZip) {
        try (FileOutputStream fos = new FileOutputStream(outputZip);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (String filePath : files) {
                File fileToZip = new File(filePath);
                try (FileInputStream fis = new FileInputStream(fileToZip)) {
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    LogsMain.logInfo("Arquivo adicionado ao ZIP: " + fileToZip.getName());
                }
            }
            LogsMain.logInfo("Compactação concluída: " + outputZip);
        } catch (IOException e) {
            LogsMain.logError("Erro ao criar ZIP: " + e.getMessage());
        }
    }
}