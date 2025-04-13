package com.scraping.java;

import com.scraping.java.compressor.FileCompressor;
import com.scraping.java.config.Config;
import com.scraping.java.directory.DirectoryManager;
import com.scraping.java.directory.PDFDownloader;

import java.io.File;
import java.util.List;

public class ScrapingMain {

    public static void main(String[] args) {
        try {
            List<Config> configs = Config.loadAll("config/config.json");

            int index = 0;

            for (Config config : configs) {
                index++;
                System.out.println("\n\n" + "\n\u001B[96m" + "=== Processando configuração " + "#" + index + " ===\n" + "\u001B[0m");
                LogsMain.logInfo("Processando: " + config.url);

                DirectoryManager.createDirectory(config.downloadDirectory);

                PDFDownloader downloader = new PDFDownloader(
                        config.url,
                        config.downloadDirectory,
                        config.anexos
                );

                List<String> downloadedFiles = downloader.downloadAnexos();

                if (!downloadedFiles.isEmpty()) {
                    FileCompressor.compressFiles(downloadedFiles, config.downloadDirectory + File.separator + config.outputZip);
                } else {
                    LogsMain.logWarning("Nenhum anexo encontrado em: " + config.url);
                }
            }
        } catch (Exception e) {
            LogsMain.logError("Erro geral: " + e.getMessage());
        }
    }
}

