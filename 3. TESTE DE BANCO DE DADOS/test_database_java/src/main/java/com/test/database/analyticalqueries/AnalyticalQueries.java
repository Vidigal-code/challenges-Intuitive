package com.test.database.analyticalqueries;

import com.test.database.LogsMain;
import com.test.database.config.DataConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Scanner;

public class AnalyticalQueries {

    public void run() {
        try (
                Scanner scanner = new Scanner(System.in);
                Connection conn = DriverManager.getConnection(
                        new DataConfig().getUrl(),
                        new DataConfig().getUser(),
                        new DataConfig().getPassword()
                )
        ) {
            System.out.println("\u001B[92mDigite os parâmetros ou pressione ENTER para usar o padrão.\u001B[0m");

            System.out.print("Quantas operadoras mostrar (padrão: 10)? ");
            String inputLimit = scanner.nextLine().trim();
            int limit = inputLimit.isEmpty() ? 10 : Integer.parseInt(inputLimit);

            System.out.println("Exemplos de categorias: 'Provisão', 'EVENTOS/ SINISTROS...'");
            System.out.print("Categoria a buscar (pressione ENTER para usar o padrão):\n");
            String categoria = scanner.nextLine().trim();
            if (categoria.isEmpty()) {
                categoria = "EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR";
            }

            PreparedStatement trimestreStmt = conn.prepareStatement("""
                SELECT YEAR(data), QUARTER(data)
                FROM demonstracoes_contabeis
                ORDER BY data DESC
                LIMIT 1
            """);
            ResultSet trimestreRs = trimestreStmt.executeQuery();
            if (!trimestreRs.next()) {
                LogsMain.logError("Nenhum dado encontrado na tabela demonstracoes_contabeis.");
                return;
            }
            int ano = trimestreRs.getInt(1);
            int trimestre = trimestreRs.getInt(2);

            System.out.printf("\n\u001B[92mTop %d operadoras com maiores despesas em '%s' no trimestre %d de %d:\u001B[0m\n", limit, categoria, trimestre, ano);
            PreparedStatement trimestreQuery = conn.prepareStatement("""
                SELECT o.razao_social, SUM(dc.vl_saldo_final - dc.vl_saldo_inicial) AS total_despesa
                FROM demonstracoes_contabeis dc
                JOIN operadoras o ON o.registro_ans = dc.reg_ans
                WHERE dc.descricao LIKE ? AND YEAR(dc.data) = ? AND QUARTER(dc.data) = ?
                GROUP BY o.razao_social
                ORDER BY total_despesa DESC
                LIMIT ?
            """);
            trimestreQuery.setString(1, "%" + categoria + "%");
            trimestreQuery.setInt(2, ano);
            trimestreQuery.setInt(3, trimestre);
            trimestreQuery.setInt(4, limit);
            ResultSet trimestreResults = trimestreQuery.executeQuery();

            StringBuilder trimestreCSV = new StringBuilder("Operadora,Despesa (R$)\n");
            while (trimestreResults.next()) {
                String nome = trimestreResults.getString(1);
                double valor = trimestreResults.getDouble(2);
                System.out.printf("%s - R$ %,.2f%n", nome, valor);
                trimestreCSV.append(String.format("%s,%.2f\n", nome, valor));
            }

            System.out.printf("\n\u001B[92mTop %d operadoras com maiores despesas em '%s' no ano %d:\u001B[0m\n", limit, categoria, ano);
            PreparedStatement anoQuery = conn.prepareStatement("""
                SELECT o.razao_social, SUM(dc.vl_saldo_final - dc.vl_saldo_inicial) AS total_despesa
                FROM demonstracoes_contabeis dc
                JOIN operadoras o ON o.registro_ans = dc.reg_ans
                WHERE dc.descricao LIKE ? AND YEAR(dc.data) = ?
                GROUP BY o.razao_social
                ORDER BY total_despesa DESC
                LIMIT ?
            """);
            anoQuery.setString(1, "%" + categoria + "%");
            anoQuery.setInt(2, ano);
            anoQuery.setInt(3, limit);
            ResultSet anoResults = anoQuery.executeQuery();

            StringBuilder anoCSV = new StringBuilder("Operadora,Despesa (R$)\n");
            while (anoResults.next()) {
                String nome = anoResults.getString(1);
                double valor = anoResults.getDouble(2);
                System.out.printf("%s - R$ %,.2f%n", nome, valor);
                anoCSV.append(String.format("%s,%.2f\n", nome, valor));
            }

            System.out.println("\nDeseja exportar os resultados?");
            System.out.println("1 - Exportar resultados para .CSV");
            System.out.println("0 - Finalizar");
            String escolha = scanner.nextLine().trim();

            if (escolha.equals("1")) {
                String exportDir = "./export";
                System.out.printf("Digite o diretório ou pressione ENTER para usar o padrão (%s):%n", exportDir);
                String inputDir = scanner.nextLine().trim();
                if (!inputDir.isEmpty()) exportDir = inputDir;

                Files.createDirectories(Path.of(exportDir));

                Path trimestreFile = Path.of(exportDir, String.format("despesas_trimestre_%d_%d.csv", trimestre, ano));
                Path anoFile = Path.of(exportDir, String.format("despesas_ano_%d.csv", ano));

                try (FileWriter out = new FileWriter(trimestreFile.toFile())) {
                    out.write(trimestreCSV.toString());
                }
                try (FileWriter out = new FileWriter(anoFile.toFile())) {
                    out.write(anoCSV.toString());
                }

                LogsMain.logSuccess(String.format("Arquivos exportados com sucesso:%n- %s%n- %s",
                        trimestreFile.toString(), anoFile.toString()));
            } else {
                LogsMain.logInfo("Finalizado sem exportação.");
            }

        } catch (SQLException e) {
            LogsMain.logError("Erro ao executar análise SQL: " + e.getMessage());
        } catch (IOException e) {
            LogsMain.logError("Erro ao manipular arquivos: " + e.getMessage());
        } catch (NumberFormatException e) {
            LogsMain.logError("Valor numérico inválido: " + e.getMessage());
        } catch (Exception e) {
            LogsMain.logError("Erro inesperado: " + e.getMessage());
        }
    }
}