# Sistema de Análise de Dados da ANS

Este projeto consiste em uma ferramenta para baixar, processar e analisar dados de operadoras de saúde e demonstrações contábeis da ANS (Agência Nacional de Saúde Suplementar).

## Requisitos

- Java 17+
- MySQL 8.0+ ou PostgreSQL 10.0+
- Dependências Java:
    - dotenv-java
    - gson
    - mysql-connector-java
    - junit-jupiter (para testes)
    - mockito (para testes)

## Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/Vidigal-code/challenges-Intuitive/tree/main/3.%20TESTE%20DE%20BANCO%20DE%20DADOS/test_database_java.git
   cd ans-data-analysis
   ```

2. Compile o projeto com Maven:
   ```bash
   mvn clean install
   ```

3. Configure o ambiente:
    - Crie um arquivo `src/main/resources/.env` na raiz do projeto com as seguintes variáveis:
      ```
      MYSQL_HOST=localhost
      MYSQL_PORT=3306
      MYSQL_USER=root
      MYSQL_PASSWORD=Kzlui_Xga4wO
      MYSQL_DATABASE=ans_test_db
      ```

4. Crie os diretórios necessários:
   ```bash
   mkdir -p downloads/demonstracoes/2024
   mkdir -p downloads/operadoras
   mkdir -p export
   ```

5. Configure os arquivos de configuração:
    - Crie um diretório `config` e adicione os arquivos:
        - `config.json`:
          ```json
          [
            {
              "type": "demonstracoes",
              "source": "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/",
              "output": "downloads/demonstracoes",
              "encoding": "utf8mb4",
              "ano": ["2024"],
              "filter": [""]
            },
            {
              "type": "operadoras",
              "source": "https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/",
              "output": "downloads/operadoras",
              "encoding": "utf8mb4",
              "filter": [""]
            }
          ]
          ```
        - `import_csv_data.json`:
          ```json
          {
            "paths": {
              "demonstracoes_csv": [
                "downloads/demonstracoes/2024/1T2024.csv",
                "downloads/demonstracoes/2024/2T2024.csv"
              ],
              "operadoras_csv": [
                "downloads/operadoras/Relatorio_cadop.csv"
              ]
            }
          }
          ```

## Configuração das Opções de filter

No arquivo `config.json`, as opções `filter` e `ano` controlam quais dados serão baixados e processados:

### Opção `filter`

A opção `filter` permite especificar subconjuntos específicos de arquivos para download:

- Para demonstrações contábeis:
    - `[""]`: Baixa todos os trimestres disponíveis nos anos especificados
    - `["1T2024"]`: Baixa apenas o primeiro trimestre de 2024
    - `["1T2024", "2T2024"]`: Baixa o primeiro e segundo trimestre de 2024
    - Formato dos filters: `["{trimestre}T{ano}"]` (exemplo: "1T2024" para 1º trimestre de 2024)

- Para operadoras:
    - `[""]`: Baixa todos os arquivos disponíveis
    - `["Relatorio_cadop"]`: Baixa apenas o arquivo de relatório cadastral
    - Você pode adicionar múltiplos nomes de arquivos separados por vírgula

### Opção `ano`

A opção `ano` especifica os anos dos dados a serem baixados:

- `["2024"]`: Baixa apenas dados de 2024
- `["2023", "2024"]`: Baixa dados de 2023 e 2024
- `[""]`: Comportamento pode variar, geralmente baixa o ano atual

Exemplo de configuração para múltiplos anos e trimestres específicos:
```json
{
  "type": "demonstracoes",
  "source": "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/",
  "output": "downloads/demonstracoes",
  "encoding": "utf8mb4",
  "ano": ["2023", "2024"],
  "filter": ["1T2023", "3T2023", "1T2024"]
}
```

Esta configuração baixaria apenas o 1º e 3º trimestres de 2023 e o 1º trimestre de 2024.

## Configuração do MySQL

### Habilitar `local_infile`

Para permitir a importação de arquivos locais no MySQL, você precisa habilitar a opção `local_infile`.

#### Configuração Permanente

1. Edite o arquivo `my.cnf` ou `my.ini`:
   ```ini
   [mysqld]
   local_infile=1
   ```

2. Reinicie o MySQL:
    - Linux: `sudo systemctl restart mysql`
    - Windows: `net stop mysql && net start mysql`

#### Configuração Temporária

Execute no MySQL:
```sql
SET GLOBAL local_infile = 1;
```

#### Verificar Configuração

```sql
SHOW GLOBAL VARIABLES LIKE 'local_infile';
-- ou
SELECT @@GLOBAL.local_infile;
```

#### Conectar com Suporte a local_infile

```bash
mysql --local-infile=1 -u usuario -p
```

## Estrutura do Projeto

A estrutura real do projeto, conforme implementada:

```
test_database_java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── test/
│   │   │           └── database/
│   │   │               ├── Main.java
│   │   │               ├── LogsMain.java
│   │   │               ├── config/
│   │   │               │   └── DataConfig.java
│   │   │               ├── downloader/
│   │   │               │   └── DataDownloader.java
│   │   │               ├── Importer/
│   │   │               │   ├── DataImporter.java
│   │   │               │   └── DataImporterAlternative.java
│   │   │               └── analyticalqueries/
│   │   │                   └── AnalyticalQueries.java
│   │   └── resources/
│   │       └── .env
│   └── test/
│       └── java/
│           └── com/
│               └── test/
│                   └── database/
│                       └── DatabaseTest.java
├── config/
│   ├── config.json
│   └── import_csv_data.json
├── downloads/
│   ├── demonstracoes/
│   └── operadoras/
├── export/
├── pom.xml
└── README.md
```

## Funcionalidades

### 1. Download de Dados

A classe `DataDownloader.java` baixa os arquivos necessários:
- Demonstrações contábeis da ANS (arquivos trimestrais)
- Dados cadastrais das operadoras ativas

### 2. Criação de Tabelas

O sistema cria duas tabelas principais:
- `operadoras`: para armazenar os dados das operadoras de saúde
- `demonstracoes_contabeis`: para armazenar os dados financeiros

### 3. Importação de Dados

Dois métodos disponíveis:
- Método principal: Via `DataImporter.java`
- Método alternativo: Via `DataImporterAlternative.java`

### 4. Análises

A classe `AnalyticalQueries.java` fornece consultas analíticas para:
- Top 10 operadoras com maiores despesas em eventos/sinistros médico-hospitalares no último trimestre
- Top 10 operadoras com maiores despesas nessa categoria no último ano

## Testes

O projeto inclui testes unitários implementados com JUnit 5 e Mockito. A classe `DatabaseTest.java` contém testes para verificar:

- Criação correta das tabelas
- Importação de dados das operadoras
- Importação de dados das demonstrações contábeis
- Carregamento da configuração
- Tratamento de diretórios inexistentes
- Manipulação de exceções SQL

## Configuração do Maven (pom.xml)

O projeto usa Maven para gerenciamento de dependências. O arquivo `pom.xml` inclui:

```xml
<dependencies>
  <dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
  </dependency>
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
  </dependency>
  <dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.0.0</version>
  </dependency>
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

## Uso

Execute o programa principal:
```bash
java -jar test_database_java.jar
```

Menu de opções disponível em `Main.java`:
1. Baixar arquivos
2. Criar tabelas e importar dados
3. Rodar queries analíticas
4. Usar script alternativo para importação
0. Sair

## Exemplo de Uso Completo

```bash
# 1. Compilar o projeto
mvn clean package

# 2. Configurar o ambiente
# (criar .env e arquivos de configuração conforme instruções acima)

# 3. Habilitar local_infile no MySQL
mysql -u root -p -e "SET GLOBAL local_infile = 1;"

# 4. Executar o programa
java -cp target/test_database_java-1.0-SNAPSHOT.jar com.test.database.Main
# Selecione as opções 1, 2 e 3 em sequência
```

## Notas

- Certifique-se de que o MySQL está configurado para aceitar arquivos locais (`local_infile=1`).
- Os arquivos baixados podem ser grandes; verifique o espaço em disco.
- Os tempos de importação dependem do volume de dados e das configurações do seu sistema.
- O projeto requer Java 17+ devido ao uso de text blocks na criação das tabelas.
