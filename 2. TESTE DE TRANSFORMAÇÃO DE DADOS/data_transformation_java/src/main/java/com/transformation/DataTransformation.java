package com.transformation;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transformation.extractor.Extractor;
import com.transformation.transformer.Transformer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DataTransformation {

    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File configFile = new File("config/config.json");
            if (!configFile.exists()) {
                LogsMain.logError("Arquivo de configuração não encontrado: " + configFile.getPath());
                return;
            }

            List<Map<String, Object>> configs = objectMapper.readValue(
                    configFile,
                    new TypeReference<List<Map<String, Object>>>() {
                    }
            );

            for (int i = 0; i < configs.size(); i++) {
                System.out.println("\n\u001B[96m=== Processando configuração #" + (i + 1) + " ===\u001B[0m");

                Map<String, Object> config = configs.get(i);
                String pdfPath = (String) config.get("pdf_path");
                String csvOutput = (String) config.get("csv_output");
                String zipOutput = (String) config.get("zip_output");
                Map<String, String> replacements = (Map<String, String>) config.get("replacements");

                try {
                    LogsMain.logInfo("Processando: " + pdfPath);
                    List<List<String>> tableData = Extractor.extractTablesFromPDF(pdfPath);
                    if (tableData.isEmpty()) {
                        LogsMain.logError("Nenhuma tabela encontrada no PDF.");
                        throw new Exception("--");
                    }

                    List<List<String>> transformedData = Transformer.applyReplacements(tableData, replacements);
                    Transformer.saveToCSV(transformedData, csvOutput);
                    Transformer.zipFile(csvOutput, zipOutput);

                    LogsMain.logInfo("Sucesso ao processar: " + pdfPath);
                } catch (Exception e) {
                    LogsMain.logError("Falha ao processar " + pdfPath + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            LogsMain.logError("Falha ao carregar o arquivo de configuração: " + e.getMessage());
        }
    }
}
