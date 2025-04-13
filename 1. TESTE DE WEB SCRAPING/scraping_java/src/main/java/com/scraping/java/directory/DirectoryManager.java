package com.scraping.java.directory;

import com.scraping.java.LogsMain;

import java.io.File;

public class DirectoryManager {
    public static void createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
            LogsMain.logInfo("Diretório criado: " + path);
        } else {
            LogsMain.logWarning("Diretório já existe: " + path);
        }
    }
}