package com.test.database.Importer;

import com.test.database.LogsMain;
import com.test.database.config.DataConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataImporter {

    private final Connection conn;
    private final List<Map<String, Object>> sources;

    public DataImporter(DataConfig config) throws SQLException {
        this.sources = config.getSources();
        this.conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
        this.conn.setAutoCommit(false);
    }

    public void importarDados() {
        for (Map<String, Object> source : sources) {
            String tipo = (String) source.get("type");
            String pasta = (String) source.get("output");
            String encoding = (String) source.getOrDefault("encoding", "utf8mb4");

            List<String> filter = (List<String>) source.getOrDefault("filter", List.of(""));
            List<String> anos = (List<String>) source.getOrDefault("ano", List.of());

            if (!Files.isDirectory(Paths.get(pasta))) {
                LogsMain.logError("Pasta não encontrada: " + pasta);
                continue;
            }

            try {
                importarArquivos(pasta, encoding, filter, anos, tipo);
            } catch (Exception e) {
                LogsMain.logError("Falha ao importar arquivos: " + e.getMessage());
            }
        }

        try {
            conn.close();
            LogsMain.logSuccess("Conexão com o banco de dados fechada com sucesso.");
        } catch (SQLException e) {
            LogsMain.logError("Erro ao fechar conexão com o banco de dados: " + e.getMessage());
        }
    }

    private void importarArquivos(String pasta, String encoding, List<String> filtros, List<String> anos, String tipo) throws Exception {
        List<Path> arquivos = new ArrayList<>();

        if (tipo.equals("demonstracoes")) {
            Files.list(Paths.get(pasta)).forEach(anoDir -> {
                if (!anos.isEmpty() && !anos.contains(anoDir.getFileName().toString())) return;
                try {
                    Files.list(anoDir).filter(p -> p.toString().endsWith(".csv")).forEach(arquivos::add);
                } catch (IOException ignored) {}
            });
        } else {
            Files.list(Paths.get(pasta)).filter(p -> p.toString().endsWith(".csv")).forEach(arquivos::add);
        }

        for (Path file : arquivos) {
            String nomeArquivo = file.getFileName().toString().toLowerCase();

            if (!filtros.isEmpty() && filtros.stream().noneMatch(f -> nomeArquivo.contains(f.toLowerCase()))) {
                continue;
            }

            LogsMain.logInfo("Importando via LOAD DATA LOCAL INFILE: " + file);

            String tabela = tipo.equals("operadoras") ? "operadoras" : "demonstracoes_contabeis";
            String colunas = tipo.equals("operadoras")
                    ? "(registro_ans, cnpj, razao_social, nome_fantasia, modalidade, logradouro, numero, complemento, bairro, cidade, uf, cep, ddd, telefone, fax, endereco_eletronico, representante, cargo_representante, regiao_de_comercializacao, data_registro_ans)"
                    : "(data, reg_ans, cd_conta_contabil, descricao, vl_saldo_inicial, vl_saldo_final)";

            String query = String.format("""
                LOAD DATA LOCAL INFILE '%s'
                INTO TABLE %s
                CHARACTER SET %s
                FIELDS TERMINATED BY ';'
                ENCLOSED BY '"'
                LINES TERMINATED BY '\\n'
                IGNORE 1 LINES
                %s
            """, file.toAbsolutePath().toString().replace("\\", "\\\\"), tabela, encoding, colunas);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(query);
                conn.commit();
                LogsMain.logSuccess("Arquivo importado com sucesso: " + file.getFileName());
            } catch (SQLException e) {
                LogsMain.logError("Erro ao importar arquivo " + file + ": " + e.getMessage());
            }
        }
    }

    public static boolean createTables(DataConfig config) {
        Path sqlPath = Paths.get("sqlfile", "create_tables.sql");

        try (Connection conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false);

            ResultSet rs = conn.getMetaData().getTables(null, null, "operadoras", null);
            boolean operadorasExists = rs.next();
            rs = conn.getMetaData().getTables(null, null, "demonstracoes_contabeis", null);
            boolean demonstracoesExists = rs.next();

            if (operadorasExists && demonstracoesExists) {
                LogsMain.logInfo("As tabelas já existem.");
                return true;
            }

            if (Files.exists(sqlPath)) {
                LogsMain.logInfo("Executando SQL: " + sqlPath);

                String sql = Files.readString(sqlPath);
                for (String part : sql.split(";")) {
                    if (!part.trim().isEmpty()) stmt.execute(part.trim());
                }

                conn.commit();
                LogsMain.logSuccess("Tabelas criadas via script.");
            } else {
                LogsMain.logWarning("Script SQL não encontrado. Criando tabelas manualmente...");

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS operadoras (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        registro_ans VARCHAR(20), cnpj VARCHAR(18), razao_social VARCHAR(255),
                        nome_fantasia VARCHAR(255), modalidade VARCHAR(100), logradouro VARCHAR(255),
                        numero VARCHAR(20), complemento VARCHAR(100), bairro VARCHAR(100),
                        cidade VARCHAR(100), uf CHAR(2), cep VARCHAR(9), ddd VARCHAR(3),
                        telefone VARCHAR(30), fax VARCHAR(30), endereco_eletronico VARCHAR(255),
                        representante VARCHAR(255), cargo_representante VARCHAR(255),
                        regiao_de_comercializacao VARCHAR(100), data_registro_ans DATE
                    );
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS demonstracoes_contabeis (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        data DATE, reg_ans VARCHAR(20), cd_conta_contabil VARCHAR(20),
                        descricao VARCHAR(255), vl_saldo_inicial DECIMAL(18,2),
                        vl_saldo_final DECIMAL(18,2)
                    );
                """);

                conn.commit();
                LogsMain.logSuccess("Tabelas criadas manualmente.");
            }

            return true;
        } catch (Exception e) {
            LogsMain.logError("Erro ao criar tabelas: " + e.getMessage());
            return false;
        }
    }
}