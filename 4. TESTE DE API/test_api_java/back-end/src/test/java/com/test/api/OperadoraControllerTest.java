package com.test.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class OperadoraControllerTest {

    private OperadoraController controller;

    private JdbcTemplate jdbcTemplate;

    private String tempCsvFilePath;

    @BeforeEach
    void setUp() throws IOException {
        controller = new OperadoraController();

        jdbcTemplate = mock(JdbcTemplate.class);
        ReflectionTestUtils.setField(controller, "jdbcTemplate", jdbcTemplate);

        tempCsvFilePath = System.getProperty("java.io.tmpdir") + "/test-operadoras.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFilePath))) {
            writer.write("Registro_ANS;CNPJ;Razao_Social;Nome_Fantasia;Modalidade;Logradouro;Numero;Complemento;Bairro;Cidade;UF;CEP;DDD;Telefone;Fax;Endereco_Eletronico;Representante;Cargo_Representante;Regiao_de_Comercializacao;Data_Registro_ANS\n");
            writer.write("123456;12.345.678/0001-00;Operadora Teste SA;Teste Saúde;Medicina de Grupo;Rua Teste;123;Sala 1;Centro;São Paulo;SP;12345-678;11;1234-5678;1234-5679;teste@operadora.com.br;João Silva;Diretor;Nacional;01/01/2000\n");
            writer.write("654321;98.765.432/0001-00;Operadora Exemplo LTDA;Exemplo Saúde;Cooperativa Médica;Av Exemplo;456;Andar 5;Jardim;Rio de Janeiro;RJ;87654-321;21;8765-4321;8765-4322;exemplo@operadora.com.br;Maria Souza;Presidente;Sudeste;02/02/2005\n");
        }
        ReflectionTestUtils.setField(controller, "csvFilePath", tempCsvFilePath);
    }

    @Test
    void testStatusEndpoint() {
        ResponseEntity<?> response = controller.status();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("online", body.get("status"));
    }

    @Test
    void testGetCampos() {
        ResponseEntity<?> response = controller.getCampos();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);

        @SuppressWarnings("unchecked")
        List<Map<String, String>> campos = (List<Map<String, String>>) body.get("campos");
        assertNotNull(campos);
        assertFalse(campos.isEmpty());
        assertEquals("", campos.get(0).get("value"));
        assertEquals("Todos os campos", campos.get(0).get("label"));
    }

    @Test
    void testSearchOperadorasDbWithEmptyParams() {
        ResponseEntity<?> response = controller.searchOperadorasDb(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Termo de busca ou filtro necessário", body.get("error"));
    }

    @Test
    void testSearchOperadorasDbWithShortQuery() {
        ResponseEntity<?> response = controller.searchOperadorasDb("a", null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Termo de busca muito curto", body.get("error"));
    }




    @Test
    void testSearchOperadorasCsvWithValidQuery() throws IOException {
        ResponseEntity<?> response = controller.searchOperadorasCsv("Teste", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void testSearchOperadorasCsvWithFilter() throws IOException {
        ResponseEntity<?> response = controller.searchOperadorasCsv(null, "modalidade");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }
}