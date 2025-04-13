package com.test.database;

import com.test.database.config.DataConfig;
import com.test.database.Importer.DataImporter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    @TempDir
    Path tempDir;

    private Connection testConnection;
    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/ans_test_db?allowLoadLocalInfile=true";
    private static final String TEST_DB_USER = "root";
    private static final String TEST_DB_PASSWORD = "Kzlui_Xga4wO";


    private static class TestDataConfig extends DataConfig {
        private final String url;
        private final String user;
        private final String password;
        private final List<Map<String, Object>> sources;

        public TestDataConfig(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
            this.sources = new ArrayList<>();
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getUser() {
            return user;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public List<Map<String, Object>> getSources() {
            return sources;
        }

        public void addSource(Map<String, Object> source) {
            sources.add(source);
        }
    }

    private TestDataConfig testConfig;

    @BeforeEach
    void setUp() throws Exception {
        testConnection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
        testConnection.setAutoCommit(false);

        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("""
                    
                    CREATE TABLE IF NOT EXISTS operadoras (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           registro_ans VARCHAR(20),
                           cnpj VARCHAR(18),
                           razao_social VARCHAR(255),
                           nome_fantasia VARCHAR(255),
                           modalidade VARCHAR(100),
                           logradouro VARCHAR(255),
                           numero VARCHAR(20),
                           complemento VARCHAR(100),
                           bairro VARCHAR(100),
                           cidade VARCHAR(100),
                           uf CHAR(2),
                           cep VARCHAR(9),
                           ddd VARCHAR(3),
                           telefone VARCHAR(30),\s
                           fax VARCHAR(30),     \s
                           endereco_eletronico VARCHAR(255),
                           representante VARCHAR(255),\s
                           cargo_representante VARCHAR(255),\s
                           regiao_de_comercializacao VARCHAR(100),\s
                           data_registro_ans DATE
                       )
                    """);

            stmt.execute("""
                    
                       CREATE TABLE IF NOT EXISTS demonstracoes_contabeis (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        data DATE,
                        reg_ans VARCHAR(20),
                        cd_conta_contabil VARCHAR(20),
                        descricao VARCHAR(255),
                        vl_saldo_inicial DECIMAL(18,2),
                        vl_saldo_final DECIMAL(18,2)
                    )
                    """);

            testConnection.commit();
        }
        testConfig = new TestDataConfig(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS operadoras");
            stmt.execute("DROP TABLE IF EXISTS demonstracoes_contabeis");
            testConnection.commit();
        } finally {
            if (testConnection != null && !testConnection.isClosed()) {
                testConnection.close();
            }
        }
    }

    @Test
    @DisplayName("Test database tables creation")
    void testCreateTables() {
        assertTrue(DataImporter.createTables(testConfig), "Table creation should succeed");

        try (Statement stmt = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD).createStatement()) {
            ResultSet rsOperadoras = stmt.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'OPERADORAS'");
            rsOperadoras.next();
            assertEquals(1, rsOperadoras.getInt(1), "Operadoras table should exist");

            ResultSet rsDemonstracoes = stmt.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'DEMONSTRACOES_CONTABEIS'");
            rsDemonstracoes.next();
            assertEquals(1, rsDemonstracoes.getInt(1), "Demonstracoes_contabeis table should exist");
        } catch (Exception e) {
            fail("Failed to verify tables: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test importing operadoras data")
    void testImportOperadorasData() throws Exception {
        Path testCsvFile = tempDir.resolve("test_operadoras.csv");
        Files.writeString(testCsvFile,
                "registro_ans;cnpj;razao_social;nome_fantasia;modalidade;logradouro;numero;complemento;bairro;cidade;uf;cep;ddd;telefone;fax;endereco_eletronico;representante;cargo_representante;regiao_de_comercializacao;data_registro_ans\n" +
                        "123456;12345678901234;Operadora Teste;Nome Fantasia;Cooperativa Médica;Rua Teste;123;Sala 1;Centro;São Paulo;SP;01234567;11;12345678;12345678;teste@email.com;João Silva;Diretor;Sudeste;2020-01-01"
        );
        Map<String, Object> source = new HashMap<>();
        source.put("type", "operadoras");
        source.put("output", tempDir.toString());
        source.put("encoding", "utf8");
        testConfig.addSource(source);
        DataImporter importer = new DataImporter(testConfig);
        importer.importarDados();
        try (Statement stmt = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD).createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM operadoras")) {
            rs.next();
            int count = rs.getInt(1);
            assertTrue(count > 0, "Should have imported operadoras data");
        }
    }

    @Test
    @DisplayName("Test importing demonstracoes contabeis data")
    void testImportDemonstracoesContabeisData() throws Exception {
        Path anoDir = tempDir.resolve("2022");
        Files.createDirectories(anoDir);
        Path testCsvFile = anoDir.resolve("demonstracoes_2022.csv");
        Files.writeString(testCsvFile,
                "data;reg_ans;cd_conta_contabil;descricao;vl_saldo_inicial;vl_saldo_final\n" +
                        "2022-01-01;123456;12345;EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR;1000.00;2000.00"
        );
        Map<String, Object> source = new HashMap<>();
        source.put("type", "demonstracoes");
        source.put("output", tempDir.toString());
        source.put("encoding", "utf8");
        List<String> anos = new ArrayList<>();
        anos.add("2022");
        source.put("ano", anos);
        testConfig.addSource(source);
        DataImporter importer = new DataImporter(testConfig);
        importer.importarDados();
        try (Statement stmt = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD).createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM demonstracoes_contabeis")) {
            rs.next();
            int count = rs.getInt(1);
            assertTrue(count > 0, "Should have imported demonstracoes contabeis data");
        }
    }

    @Test
    @DisplayName("Test data config loads properly")
    void testDataConfigLoading() {
        try {
            DataConfig config = new DataConfig();
            assertNotNull(config.getUrl(), "Database URL should not be null");
            assertNotNull(config.getUser(), "Database user should not be null");
            assertFalse(config.getSources().isEmpty(), "Sources should not be empty");
        } catch (Exception e) {
            fail("DataConfig instantiation failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test handling missing directory")
    void testHandleMissingDirectory() throws SQLException, IOException {
        Map<String, Object> source = new HashMap<>();
        source.put("type", "operadoras");
        source.put("output", "/non/existent/directory");
        testConfig.addSource(source);
        DataImporter importer = new DataImporter(testConfig);
        assertDoesNotThrow(() -> importer.importarDados(), "Should handle missing directory gracefully");
    }

    @Test
    @DisplayName("Test SQL exception handling")
    void testSqlExceptionHandling() {
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            assertThrows(SQLException.class, () -> {
                stmt.executeQuery("SELECT * FROM non_existent_table");
            }, "Should throw SQLException for non-existent table");
        } catch (SQLException e) {
            fail("Error setting up SQL exception test: " + e.getMessage());
        }
    }
}