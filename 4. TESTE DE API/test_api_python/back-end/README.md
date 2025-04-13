# Documentação do Back-end - Sistema de Busca de Operadoras de Saúde

## Visão Geral

O back-end do sistema de busca de operadoras de saúde foi desenvolvido em Python utilizando o framework Flask. Ele fornece uma API RESTful que permite buscar operadoras de saúde tanto em um banco de dados MySQL quanto em um arquivo CSV.

## Funcionalidades Principais

1. **Busca em Múltiplas Fontes**: 
   - Banco de dados MySQL
   - Arquivo CSV

2. **Sistema de Filtros**:
   - Permite buscar em campos específicos
   - Suporta busca em todos os campos quando nenhum filtro é especificado

3. **Cálculo de Relevância**:
   - Ranking de resultados baseado na similaridade com o termo buscado

## Estrutura do Código

### Configurações Iniciais

```python
import os
import csv
import pymysql
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS

load_dotenv()
app = Flask(__name__)
CORS(app)
```

Esta seção importa as bibliotecas necessárias e configura o Flask com suporte a CORS (Cross-Origin Resource Sharing), permitindo que o front-end se comunique com a API.

### Configuração do Banco de Dados

```python
db_config = {
    'host': os.getenv('DB_HOST'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASSWORD'),
    'database': os.getenv('DB_NAME'),
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}
```

As credenciais do banco de dados são carregadas de variáveis de ambiente para maior segurança.

### Mapeamento de Campos

```python
DB_FIELDS = ['registro_ans', 'cnpj', 'razao_social', 'nome_fantasia', 'cidade', 'uf', 'modalidade']
CSV_FIELDS = ['Registro_ANS', 'CNPJ', 'Razao_Social', 'Nome_Fantasia', 'Cidade', 'UF', 'Modalidade']
OUTPUT_FIELDS = ['registro_ans', 'cnpj', 'razao_social', 'nome_fantasia', 'cidade', 'uf', 'modalidade']
```

Estas listas definem o mapeamento entre os campos do banco de dados, do arquivo CSV e do formato de saída da API.

### Cálculo de Relevância

```python
def calculate_relevance(text, search_term):
    # Implementação do algoritmo de relevância
```

Esta função calcula a relevância de um resultado com base na similaridade entre o termo buscado e o conteúdo do campo.

### Padronização de Resultados

```python
def standardize_result(row, is_from_db=True):
    # Padronização dos campos de saída
```

Esta função padroniza os resultados para garantir que tanto os dados do banco de dados quanto do CSV sejam retornados no mesmo formato.

### Endpoints da API

#### 1. Busca no Banco de Dados

```python
@app.route('/api/operadoras/db', methods=['GET'])
def search_operadoras_db():
    # Implementação da busca no banco de dados
```

Este endpoint busca operadoras no banco de dados MySQL, com suporte a filtros por campo específico.

#### 2. Busca no CSV

```python
@app.route('/api/operadoras/csv', methods=['GET'])
def search_operadoras_csv():
    # Implementação da busca no arquivo CSV
```

Este endpoint busca operadoras no arquivo CSV, também com suporte a filtros.

#### 3. Status da API

```python
@app.route('/api/status', methods=['GET'])
def status():
    return jsonify({"status": "online"})
```

Este endpoint simples permite verificar se a API está online.

#### 4. Lista de Campos

```python
@app.route('/api/campos', methods=['GET'])
def get_campos():
    # Retorna a lista de campos disponíveis para filtro
```

Este endpoint fornece a lista de campos disponíveis para o front-end utilizar como opções de filtro.

## Sistema de Filtros

O sistema de filtros foi implementado da seguinte forma:

1. **Parâmetros da URL**:
   - `q`: Termo de busca (opcional se `filter` estiver presente)
   - `filter`: Campo específico para busca (opcional)

2. **Lógica de Busca**:
   - Com filtro e termo de busca: Busca apenas no campo especificado
   - Com filtro sem termo: Retorna todos os registros (limitados a 100)
   - Com termo sem filtro: Busca em todos os campos
   - Sem termo e sem filtro: Retorna erro de validação

3. **Adaptações na Query SQL**:
   ```python
   if filter_field and filter_field in DB_FIELDS:
       # Busca apenas no campo filtrado
       query = f"SELECT * FROM operadoras WHERE {filter_field} LIKE %s LIMIT 100"
   else:
       # Busca em todos os campos
       query = "SELECT * FROM operadoras WHERE campo1 LIKE %s OR campo2 LIKE %s ... LIMIT 100"
   ```

4. **Adaptações na Busca CSV**:
   ```python
   if filter_csv_field:
       # Calcula relevância apenas no campo filtrado
   else:
       # Calcula relevância em todos os campos
   ```

## Executando a Aplicação

Para executar o servidor:

```python
if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    debug = os.getenv('DEBUG', 'False').lower() == 'true'
    app.run(host='0.0.0.0', port=port, debug=debug)
```

O servidor escuta na porta definida na variável de ambiente PORT (ou 5000 por padrão).


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



## Conclusão

O back-end do sistema oferece uma API flexível para busca de operadoras de saúde, com suporte a múltiplas fontes de dados e um sistema de filtro eficiente. A implementação usando Flask proporciona uma solução leve e de fácil manutenção.


