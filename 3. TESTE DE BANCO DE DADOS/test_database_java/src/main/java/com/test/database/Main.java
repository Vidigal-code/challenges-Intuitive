package com.test.database;

import com.test.database.Importer.*;
import com.test.database.analyticalqueries.AnalyticalQueries;
import com.test.database.config.DataConfig;
import com.test.database.downloader.DataDownloader;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DataConfig config = new DataConfig();

        LogsMain.logInfo("== Projeto ANS - Banco de Dados ==");
        System.out.println("Escolha uma opção:");
        System.out.println("1. Baixar arquivos");
        System.out.println("2. Criar tabelas e importar dados");
        System.out.println("3. Rodar queries analíticas");
        System.out.println("4. Criar/importar com script alternativo (INSERT linha a linha)");
        System.out.println("0. Sair");

        System.out.print("Digite a opção desejada: ");
        String opcao = scanner.nextLine();

        try {
            switch (opcao) {
                case "1" -> {
                    LogsMain.logInfo("Baixando e extraindo arquivos...");
                    new DataDownloader().run();
                    LogsMain.logSuccess("Arquivos baixados.");
                }
                case "2" -> {
                    LogsMain.logInfo("Criando tabelas e importando dados...");
                    if (DataImporter.createTables(config)) {
                        DataImporter importer = new DataImporter(config);
                        importer.importarDados();
                    }
                    LogsMain.logSuccess("Processo de criação e importação concluído.");
                }
                case "3" -> {
                    LogsMain.logInfo("Executando análises...");
                    new AnalyticalQueries().run();
                    LogsMain.logSuccess("Análises finalizadas.");
                }
                case "4" -> {
                    LogsMain.logInfo("Rodando script alternativo para importação...");
                    if (DataImporter.createTables(config)) {
                        Map<String, List<String>> pathsMap = DataImporterAlternative.importDataJson("config/import_csv_data.json");
                        new DataImporterAlternative(config).importarPorPaths(pathsMap);
                    }
                    LogsMain.logSuccess("Importação alternativa concluída.");
                }
                case "0" -> LogsMain.logInfo("Saindo...");
                default -> LogsMain.logError("Opção inválida.");
            }
        } catch (SQLException e) {
            LogsMain.logError("SQL: " + e.getMessage());
        } catch (Exception e) {
            LogsMain.logError("Error exception " + e.getMessage());
        }
    }
}