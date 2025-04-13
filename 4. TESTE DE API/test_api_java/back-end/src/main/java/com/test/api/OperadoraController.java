package com.test.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OperadoraController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${csv.file.path}")
    private String csvFilePath;

    private static final List<String> DB_FIELDS = Arrays.asList(
            "registro_ans", "cnpj", "razao_social", "nome_fantasia", "modalidade",
            "logradouro", "numero", "complemento", "bairro", "cidade", "uf", "cep",
            "ddd", "telefone", "fax", "endereco_eletronico", "representante",
            "cargo_representante", "regiao_de_comercializacao", "data_registro_ans"
    );

    private static final List<String> CSV_FIELDS = Arrays.asList(
            "Registro_ANS", "CNPJ", "Razao_Social", "Nome_Fantasia", "Modalidade",
            "Logradouro", "Numero", "Complemento", "Bairro", "Cidade", "UF", "CEP",
            "DDD", "Telefone", "Fax", "Endereco_Eletronico", "Representante",
            "Cargo_Representante", "Regiao_de_Comercializacao", "Data_Registro_ANS"
    );

    private static final List<String> OUTPUT_FIELDS = Arrays.asList(
            "registro_ans", "cnpj", "razao_social", "nome_fantasia", "modalidade",
            "logradouro", "numero", "complemento", "bairro", "cidade", "uf", "cep",
            "ddd", "telefone", "fax", "endereco_eletronico", "representante",
            "cargo_representante", "regiao_de_comercializacao", "data_registro_ans"
    );

    private int calculateRelevance(String text, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty() || text == null || text.isEmpty()) {
            return 0;
        }

        text = text.toLowerCase();
        searchTerm = searchTerm.toLowerCase();

        if (searchTerm.equals(text)) {
            return 100;
        }

        if (text.contains(searchTerm)) {
            return 80;
        }

        String[] words = searchTerm.split("\\s+");
        int matches = (int) Arrays.stream(words).filter(text::contains).count();
        if (matches > 0) {
            return 50 + (30 * matches / words.length);
        }

        for (String part : searchTerm.split("\\s+")) {
            if (text.contains(part)) {
                return 40;
            }
        }

        return 0;
    }

    private Map<String, Object> standardizeResult(Map<String, Object> row, boolean isFromDb) {
        Map<String, Object> result = new HashMap<>();
        List<String> sourceFields = isFromDb ? DB_FIELDS : CSV_FIELDS;

        for (int i = 0; i < sourceFields.size() && i < OUTPUT_FIELDS.size(); i++) {
            String sourceField = sourceFields.get(i);
            String targetField = OUTPUT_FIELDS.get(i);
            Object value = row.getOrDefault(sourceField, "");
            result.put(targetField, value != null ? value.toString() : "");
        }

        return result;
    }

    @GetMapping("/operadoras/db")
    public ResponseEntity<?> searchOperadorasDb(@RequestParam(required = false) String q,
                                                @RequestParam(required = false) String filter) {
        if ((q == null || q.isEmpty()) && (filter == null || filter.isEmpty())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Termo de busca ou filtro necessário");
            return ResponseEntity.badRequest().body(error);
        }

        if (q != null && !q.isEmpty() && q.length() < 2) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Termo de busca muito curto");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            List<Object> params = new ArrayList<>();
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM operadoras");

            if (filter != null && !filter.isEmpty() && DB_FIELDS.contains(filter)) {
                if (q != null && !q.isEmpty()) {
                    sqlBuilder.append(" WHERE ").append(filter).append(" LIKE ?");
                    params.add("%" + q + "%");
                }
            } else if (q != null && !q.isEmpty()) {
                sqlBuilder.append(" WHERE ");
                List<String> whereClauses = new ArrayList<>();

                for (String field : DB_FIELDS) {
                    whereClauses.add(field + " LIKE ?");
                    params.add("%" + q + "%");
                }

                sqlBuilder.append(String.join(" OR ", whereClauses));
            }

            sqlBuilder.append(" LIMIT 100");
            String sql = sqlBuilder.toString();

            List<Map<String, Object>> rawResults = jdbcTemplate.queryForList(sql, params.toArray());
            List<Map<String, Object>> results = new ArrayList<>();

            for (Map<String, Object> row : rawResults) {
                Map<String, Object> result = standardizeResult(row, true);

                if (q != null && !q.isEmpty()) {
                    int relevance = 0;
                    if (filter != null && !filter.isEmpty() && DB_FIELDS.contains(filter)) {
                        Object fieldValue = row.get(filter);
                        relevance = calculateRelevance(fieldValue != null ? fieldValue.toString() : "", q);
                    } else {
                        for (String field : DB_FIELDS) {
                            Object fieldValue = row.get(field);
                            int fieldRelevance = calculateRelevance(fieldValue != null ? fieldValue.toString() : "", q);
                            relevance = Math.max(relevance, fieldRelevance);
                        }
                    }
                    result.put("relevance", relevance);
                } else {
                    result.put("relevance", 100);
                }

                results.add(result);
            }

            if (q != null && !q.isEmpty()) {
                results.sort((a, b) -> Integer.compare((int) b.get("relevance"), (int) a.get("relevance")));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("results", results);
            response.put("count", results.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.toString());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/operadoras/csv")
    public ResponseEntity<?> searchOperadorasCsv(@RequestParam(required = false) String q,
                                                 @RequestParam(required = false) String filter) {
        if ((q == null || q.isEmpty()) && (filter == null || filter.isEmpty())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Termo de busca ou filtro necessário");
            return ResponseEntity.badRequest().body(error);
        }

        if (q != null && !q.isEmpty() && q.length() < 2) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Termo de busca muito curto");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            List<Map<String, Object>> results = new ArrayList<>();
            String filterCsvField = null;

            if (filter != null && !filter.isEmpty()) {
                int index = DB_FIELDS.indexOf(filter);
                if (index >= 0 && index < CSV_FIELDS.size()) {
                    filterCsvField = CSV_FIELDS.get(index);
                }
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
                String headerLine = reader.readLine();
                String[] headers = headerLine.split(";");
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(";");
                    Map<String, Object> row = new HashMap<>();

                    for (int i = 0; i < headers.length && i < values.length; i++) {
                        row.put(headers[i], values[i]);
                    }

                    if (q != null && !q.isEmpty()) {
                        int relevance = 0;

                        if (filterCsvField != null) {
                            Object fieldValue = row.get(filterCsvField);
                            relevance = calculateRelevance(fieldValue != null ? fieldValue.toString() : "", q);
                        } else {
                            for (String field : CSV_FIELDS) {
                                Object fieldValue = row.get(field);
                                int fieldRelevance = calculateRelevance(fieldValue != null ? fieldValue.toString() : "", q);
                                relevance = Math.max(relevance, fieldRelevance);
                            }
                        }

                        if (relevance > 0) {
                            Map<String, Object> result = standardizeResult(row, false);
                            result.put("relevance", relevance);
                            results.add(result);
                        }
                    } else {
                        if (filterCsvField == null || row.containsKey(filterCsvField)) {
                            Map<String, Object> result = standardizeResult(row, false);
                            result.put("relevance", 100);
                            results.add(result);
                        }
                    }
                }
            }

            if (q != null && !q.isEmpty()) {
                results.sort((a, b) -> Integer.compare((int) b.get("relevance"), (int) a.get("relevance")));
            }

            List<Map<String, Object>> limitedResults = results.stream()
                    .limit(100)
                    .collect(Collectors.toList());

            for (Map<String, Object> result : limitedResults) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    if (entry.getValue() instanceof String) {
                        entry.setValue(((String) entry.getValue()).replace("\"", ""));
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("results", limitedResults);
            response.put("count", limitedResults.size());

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.toString());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "online");
        return ResponseEntity.ok(status);
    }

    @GetMapping("/campos")
    public ResponseEntity<?> getCampos() {
        List<Map<String, String>> campos = new ArrayList<>();

        Map<String, String> todosOsCampos = new HashMap<>();
        todosOsCampos.put("value", "");
        todosOsCampos.put("label", "Todos os campos");
        campos.add(todosOsCampos);

        for (String field : DB_FIELDS) {
            Map<String, String> campo = new HashMap<>();
            campo.put("value", field);
            campo.put("label", field.replace("_", " ").substring(0, 1).toUpperCase() +
                    field.replace("_", " ").substring(1));
            campos.add(campo);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("campos", campos);
        return ResponseEntity.ok(response);
    }

    @Configuration
    public class CorsConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .maxAge(3600);
        }
    }
}