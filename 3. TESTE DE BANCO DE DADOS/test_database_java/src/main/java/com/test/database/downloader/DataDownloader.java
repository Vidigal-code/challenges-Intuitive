package com.test.database.downloader;

import com.test.database.LogsMain;
import com.test.database.config.DataConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class DataDownloader {

    private final List<Map<String, Object>> config;

    public DataDownloader() {
        this.config = new DataConfig().getSources();
    }

    public void run() {
        for (Map<String, Object> base : config) {
            String type = (String) base.get("type");
            List<String> filter = getList(base.get("filter"));

            if ("demonstracoes".equalsIgnoreCase(type)) {
                LogsMain.logInfo("Baixando demonstrações contábeis...");
                downloadDemonstracoes(base, filter);
            } else if ("operadoras".equalsIgnoreCase(type)) {
                LogsMain.logInfo("Baixando arquivos de operadoras...");
                downloadOperadoras(base, filter);
            }
        }
    }

    private void downloadDemonstracoes(Map<String, Object> base, List<String> filter) {
        String source = (String) base.get("source");
        String output = (String) base.get("output");
        List<String> anos = getList(base.get("ano"));

        List<String> anoTrimestres = new ArrayList<>();
        if (filter != null && !filter.isEmpty() && !filter.get(0).isEmpty()) {
            anoTrimestres.addAll(filter);
        } else {
            for (String ano : anos) {
                for (int i = 1; i <= 4; i++) {
                    anoTrimestres.add(i + "T" + ano);
                }
            }
        }

        for (String item : anoTrimestres) {
            String ano = item.substring(item.length() - 4);
            String zipName = item + ".zip";
            String url = source + ano + "/" + zipName;
            String destPath = Paths.get(output, ano, zipName).toString();

            try {
                Path downloaded = downloadFile(url, destPath);
                extractZip(downloaded.toString(), Paths.get(output, ano).toString());
            } catch (Exception e) {
                LogsMain.logError("Erro ao baixar " + zipName + ": " + e.getMessage());
            }
        }
    }

    private void downloadOperadoras(Map<String, Object> base, List<String> filter) {
        String source = (String) base.get("source");
        String output = (String) base.get("output");

        List<String> arquivos = (filter != null && !filter.isEmpty() && !filter.get(0).isEmpty())
                ? filter : Collections.singletonList("Relatorio_cadop");

        for (String item : arquivos) {
            String fileName = item + ".csv";
            String url = source + fileName;
            String destPath = Paths.get(output, fileName).toString();

            try {
                downloadFile(url, destPath);
            } catch (Exception e) {
                LogsMain.logError("Erro ao baixar " + fileName + ": " + e.getMessage());
            }
        }
    }

    private Path downloadFile(String fileUrl, String destination) throws IOException {
        Path dest = Paths.get(destination);
        Files.createDirectories(dest.getParent());

        if (Files.exists(dest)) {
            LogsMain.logInfo("Já existe: " + destination);
            return dest;
        }

        LogsMain.logInfo("Baixando: " + fileUrl);
        HttpURLConnection conn = (HttpURLConnection) new URL(fileUrl).openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream(); OutputStream out = Files.newOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return dest;
    }

    private void extractZip(String zipPath, String destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newFile = Paths.get(destDir, entry.getName());
                Files.createDirectories(newFile.getParent());
                try (OutputStream fos = new FileOutputStream(newFile.toFile())) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }
        LogsMain.logSuccess("Extraído: " + zipPath);
    }

    @SuppressWarnings("unchecked")
    private List<String> getList(Object obj) {
        if (obj instanceof List<?>) {
            return (List<String>) obj;
        }
        return new ArrayList<>();
    }
}