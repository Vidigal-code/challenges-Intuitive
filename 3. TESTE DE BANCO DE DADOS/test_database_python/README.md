# Sistema de Análise de Dados da ANS

Este projeto consiste em uma ferramenta para baixar, processar e analisar dados de operadoras de saúde e demonstrações contábeis da ANS (Agência Nacional de Saúde Suplementar).

## Requisitos

- Python 3.6+
- MySQL 8.0+ ou PostgreSQL 10.0+
- Bibliotecas Python:
  - python-dotenv
  - requests
  - mysql-connector-python
  - pandas

## Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/ans-data-analysis.git
   cd ans-data-analysis
   ```

2. Instale as dependências:
   ```bash
   pip install -r requirements.txt
   ```

3. Configure o ambiente:
   - Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:
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

```
ans-data-analysis/
├── config/
│   ├── config.json
│   └── import_csv_data.json
├── downloads/
│   ├── demonstracoes/
│   └── operadoras/
├── export/
├── scripts/
│   ├── __init__.py
│   ├── data_analysis.py
│   ├── data_config.py
│   ├── data_download.py
│   ├── data_import_csv.py
│   └── data_script.py
├── sqlfile/
│   └── create_tables.sql
├── .env
├── main.py
└── requirements.txt
```

## Funcionalidades

### 1. Download de Dados

O módulo `data_download.py` baixa os arquivos necessários:
- Demonstrações contábeis da ANS (arquivos trimestrais)
- Dados cadastrais das operadoras ativas

### 2. Criação de Tabelas

O script cria duas tabelas principais:
- `operadoras`: para armazenar os dados das operadoras de saúde
- `demonstracoes_contabeis`: para armazenar os dados financeiros

### 3. Importação de Dados

Dois métodos disponíveis:
- Método eficiente: `LOAD DATA LOCAL INFILE` (via `data_script.py`)
- Método detalhado: Inserções linha a linha (via `data_import_csv.py`)

### 4. Análises

O módulo `data_analysis.py` fornece consultas analíticas para:
- Top 10 operadoras com maiores despesas em eventos/sinistros médico-hospitalares no último trimestre
- Top 10 operadoras com maiores despesas nessa categoria no último ano

## Uso

Execute o script principal:
```bash
python main.py
```

Menu de opções:
1. Baixar arquivos
2. Criar tabelas e importar dados
3. Rodar queries analíticas
4. Usar script alternativo para importação
0. Sair

## Exemplo de Uso Completo

```bash
# 1. Instalar dependências
pip install python-dotenv requests mysql-connector-python pandas

# 2. Configurar o ambiente
# (criar .env e arquivos de configuração conforme instruções acima)

# 3. Habilitar local_infile no MySQL
mysql -u root -p -e "SET GLOBAL local_infile = 1;"

# 4. Executar o programa
python main.py
# Selecione as opções 1, 2 e 3 em sequência
```

## Notas

- Certifique-se de que o MySQL está configurado para aceitar arquivos locais (`local_infile=1`).
- Os arquivos baixados podem ser grandes; verifique o espaço em disco.
- Os tempos de importação dependem do volume de dados e das configurações do seu sistema.