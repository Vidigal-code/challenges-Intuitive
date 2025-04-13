# Documentação da API do Controlador de Operadoras

Este documento fornece uma explicação abrangente da API OperadoraController, incluindo sua funcionalidade, estrutura e detalhes de implementação.

## Sumário
- [Visão Geral](#visão-geral)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Implementação do Controlador](#implementação-do-controlador)
- [Componentes Principais](#componentes-principais)
- [Endpoints da API](#endpoints-da-api)
- [Gerenciamento de Dados](#gerenciamento-de-dados)
- [Algoritmo de Busca](#algoritmo-de-busca)
- [Configuração](#configuração)
- [Testes](#testes)

## Visão Geral

A aplicação é uma API Spring Boot que fornece funcionalidade de busca para operadoras de saúde no Brasil. Ela permite aos usuários pesquisar operadoras a partir de um banco de dados ou de um arquivo CSV, com opções para filtrar por campos específicos e termos de busca.

Os dados parecem estar relacionados à ANS (Agência Nacional de Saúde Suplementar), que é a agência reguladora brasileira para planos e seguros de saúde privados.

## Estrutura do Projeto

O projeto consiste em três arquivos principais:
1. `OperadoraController.java` - O controlador principal da API
2. `OperadoraControllerTest.java` - Testes JUnit para o controlador
3. `application.properties` - Propriedades de configuração para a aplicação Spring

## Implementação do Controlador

A classe `OperadoraController` serve como um controlador REST manipulando requisições da API relacionadas às operadoras de saúde. Ela é anotada com `@RestController` e mapeia para o caminho base `/api`.

### Componentes Principais

#### Definições de Campos
```java
private static final List<String> DB_FIELDS = Arrays.asList(...);
private static final List<String> CSV_FIELDS = Arrays.asList(...);
private static final List<String> OUTPUT_FIELDS = Arrays.asList(...);
```

Estas listas definem os nomes dos campos usados em:
- Colunas do banco de dados (`DB_FIELDS`)
- Cabeçalhos do arquivo CSV (`CSV_FIELDS`)
- Campos de saída padronizados (`OUTPUT_FIELDS`)

Os campos representam propriedades das operadoras de saúde como número de registro, nome da empresa, endereço, etc.

#### Métodos Utilitários

A classe contém vários métodos utilitários:

1. `calculateRelevance(String text, String searchTerm)` - Calcula uma pontuação de relevância (0-100) entre o texto e um termo de busca, com pontuações mais altas para correspondências exatas
2. `standardizeResult(Map<String, Object> row, boolean isFromDb)` - Converte resultados do banco de dados ou CSV para um formato de saída padronizado

### Endpoints da API

O controlador define quatro endpoints principais:

#### 1. `/api/status` - Endpoint de Verificação de Saúde
Retorna uma mensagem de status simples para indicar que a API está em execução.

```java
@GetMapping("/status")
public ResponseEntity<?> status() {
    Map<String, String> status = new HashMap<>();
    status.put("status", "online");
    return ResponseEntity.ok(status);
}
```

#### 2. `/api/campos` - Endpoint de Informações de Campos
Retorna uma lista de campos disponíveis para filtragem, com seus rótulos e valores.

```java
@GetMapping("/campos")
public ResponseEntity<?> getCampos() {
    // Cria uma lista de definições de campos para a UI
    // ...
}
```

#### 3. `/api/operadoras/db` - Endpoint de Busca no Banco de Dados
Pesquisa operadoras no banco de dados com base em uma string de consulta e filtro de campo opcional.

```java
@GetMapping("/operadoras/db")
public ResponseEntity<?> searchOperadorasDb(@RequestParam(required = false) String q,
                                           @RequestParam(required = false) String filter) {
    // Valida parâmetros de entrada
    // Constrói consulta SQL com filtros
    // Executa consulta usando JdbcTemplate
    // Calcula pontuações de relevância
    // Retorna resultados ordenados
}
```

#### 4. `/api/operadoras/csv` - Endpoint de Busca no Arquivo CSV
Pesquisa operadoras em um arquivo CSV com base em uma string de consulta e filtro de campo opcional.

```java
@GetMapping("/operadoras/csv")
public ResponseEntity<?> searchOperadorasCsv(@RequestParam(required = false) String q,
                                            @RequestParam(required = false) String filter) {
    // Valida parâmetros de entrada
    // Lê arquivo CSV
    // Filtra resultados com base nos critérios
    // Calcula pontuações de relevância
    // Retorna resultados ordenados
}
```

## Gerenciamento de Dados

A aplicação suporta duas fontes de dados:

### 1. Banco de Dados
- Usa o `JdbcTemplate` do Spring para operações de banco de dados
- Geração dinâmica de consultas SQL baseadas em termos de busca e filtros
- Resultados são limitados a 100 registros
- Configuração MySQL em `application.properties`

### 2. Arquivo CSV
- Lê de um arquivo CSV especificado na propriedade `csv.file.path`
- Analisa registros CSV e aplica filtragem com base nos critérios de busca
- Resultados são limitados a 100 registros

## Algoritmo de Busca

A funcionalidade de busca usa uma abordagem baseada em relevância:

1. Uma pontuação de relevância (0-100) é calculada para cada registro com base em quão bem ele corresponde ao termo de busca
2. Correspondências perfeitas recebem uma pontuação de 100
3. Correspondências de contém recebem uma pontuação de 80
4. Correspondências parciais de palavras recebem pontuações mais baixas
5. Os resultados são ordenados por relevância (mais alta primeiro)

```java
private int calculateRelevance(String text, String searchTerm) {
    // Lógica para pontuação de relevância do texto para o termo de busca
    // ...
}
```

## Configuração

### Configuração CORS
A aplicação inclui uma configuração CORS que permite requisições de origem cruzada de qualquer origem:

```java
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
```

### Propriedades da Aplicação
```
spring.datasource.url=jdbc:mysql://localhost:3306/ans_test_db
spring.datasource.username=root
spring.datasource.password=Kzlui_Xga4wO
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
server.port=5000

csv.file.path=src/main/resources/Relatorio_cadop.csv
```

## Testes

A aplicação inclui testes JUnit abrangentes em `OperadoraControllerTest`:

### Configuração de Teste
- Cria um mock de `JdbcTemplate`
- Gera um arquivo CSV temporário para testes
- Injeta dependências usando `ReflectionTestUtils` do Spring

### Casos de Teste
1. `testStatusEndpoint` - Verifica se o endpoint de status retorna "online"
2. `testGetCampos` - Verifica se o endpoint de campos retorna a lista correta de campos
3. `testSearchOperadorasDbWithEmptyParams` - Verifica o tratamento de erros para parâmetros ausentes
4. `testSearchOperadorasDbWithShortQuery` - Verifica o tratamento de erros para consultas muito curtas
5. `testSearchOperadorasCsvWithValidQuery` - Verifica se a busca CSV retorna resultados para consultas válidas
6. `testSearchOperadorasCsvWithFilter` - Verifica a busca CSV com filtragem de campo

### Abordagem de Testes
- Usa Mockito para simular dependências
- Testa diretamente endpoints do controlador
- Verifica casos de sucesso e erro
- Verifica códigos de resposta e conteúdo do corpo


### Endpoints de Busca

- **Base URL**: `http://localhost:5000/api`
- **Modos**:
    - Banco de Dados: `/operadoras/db`
    - CSV: `/operadoras/csv`

#### Parâmetros:
- `q`: Termo de busca
- `campo` (opcional): Filtro de campo específico

#### Exemplos:

- **Com Filtro**:
    - Banco de Dados:  
      `http://localhost:5000/api/operadoras/db?q=12345678000195&campo=cnpj`

    - CSV:  
      `http://localhost:5000/api/operadoras/csv?q=UNIMED&campo=razao_social`

- **Sem Filtro**:
    - Banco de Dados:  
      `http://localhost:5000/api/operadoras/db?q=UNIMED`

    - CSV:  
      `http://localhost:5000/api/operadoras/csv?q=UNIMED`