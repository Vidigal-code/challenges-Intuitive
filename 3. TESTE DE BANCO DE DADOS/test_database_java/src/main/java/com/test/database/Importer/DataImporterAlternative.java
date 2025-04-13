package com.test.database.Importer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.database.LogsMain;
import com.test.database.config.DataConfig;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class DataImporterAlternative {

    private final Connection conn;

    public DataImporterAlternative(DataConfig config) {
        try {
            this.conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
        } catch (SQLException e) {
            LogsMain.logError("Erro ao conectar ao banco de dados: " + e.getMessage());
            throw new RuntimeException("Erro ao conectar ao banco de dados", e);
        }
    }

    public void importarPorPaths(Map<String, List<String>> pathsMap) {
        try {
            importarArquivos(pathsMap.get("operadoras_csv"), "operadoras");
            importarArquivos(pathsMap.get("demonstracoes_csv"), "demonstracoes_contabeis");

            if (conn != null && !conn.isClosed()) {
                conn.close();
                LogsMain.logSuccess("Conexão com o banco de dados fechada com sucesso.");
            }
        } catch (SQLException e) {
            LogsMain.logError("Erro SQL durante a importação: " + e.getMessage());
        }
    }

    private void importarArquivos(List<String> arquivos, String tabela) {
        if (arquivos == null || arquivos.isEmpty()) return;

        for (String arquivoPath : arquivos) {
            Path arquivo = Paths.get(arquivoPath);
            if (!Files.exists(arquivo)) {
                LogsMain.logError("Arquivo não encontrado: " + arquivo);
                continue;
            }

            LogsMain.logInfo("Importando: " + arquivo);

            String colunas = tabela.equals("operadoras") ? "(registro_ans, cnpj, razao_social, nome_fantasia, modalidade, logradouro, numero, complemento, bairro, cidade, uf, cep, ddd, telefone, fax, endereco_eletronico, representante, cargo_representante, regiao_de_comercializacao, data_registro_ans)"
                    : "(data, reg_ans, cd_conta_contabil, descricao, vl_saldo_inicial, vl_saldo_final)";

            String sql = String.format("""
                LOAD DATA LOCAL INFILE '%s'
                INTO TABLE %s
                CHARACTER SET utf8mb4
                FIELDS TERMINATED BY ';'
                ENCLOSED BY '"'
                LINES TERMINATED BY '\n'
                IGNORE 1 LINES
                %s
            """, arquivo.toAbsolutePath().toString().replace("\\", "/"), tabela, colunas);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET sql_mode='';");
                stmt.execute(sql);
                LogsMain.logSuccess("Dados importados: " + arquivo);
            } catch (SQLException e) {
                LogsMain.logError("Ao importar " + arquivo + ": " + e.getMessage());
            }
        }
    }

    public static Map<String, List<String>> importDataJson(String jsonPath) {
        try (FileReader reader = new FileReader(jsonPath)) {
            Type type = new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType();
            Map<String, Map<String, List<String>>> fullMap = new Gson().fromJson(reader, type);
            return fullMap.getOrDefault("paths", Collections.emptyMap());
        } catch (Exception e) {
            LogsMain.logError("Erro ao carregar arquivo JSON: " + e.getMessage());
            throw new RuntimeException("Erro ao carregar import_csv_data.json", e);
        }
    }

    public static Map<String, List<String>> importDataJsonFromDefaultPath() {
        return importDataJson(Paths.get("config", "import_csv_data.json").toString());
    }
}
