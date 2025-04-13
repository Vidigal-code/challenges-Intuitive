import os
import csv
import pymysql
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS

load_dotenv()

app = Flask(__name__)

CORS(app)

db_config = {
    'host': os.getenv('DB_HOST'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASSWORD'),
    'database': os.getenv('DB_NAME'),
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}

DB_FIELDS = [
    'registro_ans', 'cnpj', 'razao_social', 'nome_fantasia', 'modalidade', 
    'logradouro', 'numero', 'complemento', 'bairro', 'cidade', 'uf', 'cep', 
    'ddd', 'telefone', 'fax', 'endereco_eletronico', 'representante', 
    'cargo_representante', 'regiao_de_comercializacao', 'data_registro_ans'
]

CSV_FIELDS = [
    'Registro_ANS', 'CNPJ', 'Razao_Social', 'Nome_Fantasia', 'Modalidade', 
    'Logradouro', 'Numero', 'Complemento', 'Bairro', 'Cidade', 'UF', 'CEP', 
    'DDD', 'Telefone', 'Fax', 'Endereco_Eletronico', 'Representante', 
    'Cargo_Representante', 'Regiao_de_Comercializacao', 'Data_Registro_ANS'
]

OUTPUT_FIELDS = [
    'registro_ans', 'cnpj', 'razao_social', 'nome_fantasia', 'modalidade', 
    'logradouro', 'numero', 'complemento', 'bairro', 'cidade', 'uf', 'cep', 
    'ddd', 'telefone', 'fax', 'endereco_eletronico', 'representante', 
    'cargo_representante', 'regiao_de_comercializacao', 'data_registro_ans'
]

def calculate_relevance(text, search_term):
    if not search_term or not text:
        return 0

    text = str(text).lower()
    search_term = search_term.lower()

    if search_term == text:
        return 100

    if search_term in text:
        return 80

    words = search_term.split()
    matches = sum(1 for word in words if word in text)
    if matches > 0:
        return 50 + (30 * matches / len(words))

    if any(part in text for part in search_term.split()):
        return 40

    return 0

def standardize_result(row, is_from_db=True):
    result = {}
    source_fields = DB_FIELDS if is_from_db else CSV_FIELDS
    
    for i, field in enumerate(source_fields):
        if i < len(OUTPUT_FIELDS):  
            target_field = OUTPUT_FIELDS[i]
            result[target_field] = row.get(field, '')
        
    return result

@app.route('/api/operadoras/db', methods=['GET'])
def search_operadoras_db():
    search_term = request.args.get('q', '')
    filter_field = request.args.get('filter', '')
    
    if not search_term and not filter_field:
        return jsonify({"error": "Termo de busca ou filtro necessário"}), 400
    
    if search_term and len(search_term) < 2:
        return jsonify({"error": "Termo de busca muito curto"}), 400
    
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        if filter_field and filter_field in DB_FIELDS:
            if search_term:
                query = f"""
                SELECT * FROM operadoras 
                WHERE {filter_field} LIKE %s
                LIMIT 100
                """
                search_pattern = f"%{search_term}%"
                params = [search_pattern]
            else:
                query = "SELECT * FROM operadoras LIMIT 100"
                params = []
        else:
            if search_term:
                where_clauses = []
                params = []
                
                for field in DB_FIELDS:
                    where_clauses.append(f"{field} LIKE %s")
                    params.append(f"%{search_term}%")
                
                query = f"""
                SELECT * FROM operadoras 
                WHERE {' OR '.join(where_clauses)}
                LIMIT 100
                """
            else:
                query = "SELECT * FROM operadoras LIMIT 100"
                params = []
        
        cursor.execute(query, params)
        
        raw_results = cursor.fetchall()
        conn.close()
        
        results = []
        for row in raw_results:
            result = standardize_result(row, is_from_db=True)
            
            if search_term:
                relevance = 0
                if filter_field and filter_field in DB_FIELDS:
                    field_relevance = calculate_relevance(row.get(filter_field, ''), search_term)
                    relevance = field_relevance
                else:
                    for field in DB_FIELDS:
                        field_relevance = calculate_relevance(row.get(field, ''), search_term)
                        relevance = max(relevance, field_relevance)
                
                result['relevance'] = relevance
            else:
                result['relevance'] = 100
                
            results.append(result)
        
        if search_term:
            results = sorted(results, key=lambda x: x['relevance'], reverse=True)
        
        return jsonify({"results": results, "count": len(results)})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/operadoras/csv', methods=['GET'])
def search_operadoras_csv():
    search_term = request.args.get('q', '')
    filter_field = request.args.get('filter', '')
    
    if not search_term and not filter_field:
        return jsonify({"error": "Termo de busca ou filtro necessário"}), 400
    
    if search_term and len(search_term) < 2:
        return jsonify({"error": "Termo de busca muito curto"}), 400

    try:
        csv_file_name = os.getenv('CSV_FILE', 'Relatorio_cadop') + '.csv'
        base_dir = os.path.abspath(os.path.dirname(__file__))
        csv_path = os.path.join(base_dir, csv_file_name)

        results = []
        
        filter_csv_field = None
        if filter_field:
            for i, field in enumerate(DB_FIELDS):
                if field == filter_field:
                    filter_csv_field = CSV_FIELDS[i]
                    break

        with open(csv_path, 'r', encoding='utf-8') as file:
            reader = csv.DictReader(file, delimiter=';')

            for row in reader:
                if search_term:
                    relevance = 0
                    
                    if filter_csv_field:
                        if filter_csv_field in row:
                            relevance = calculate_relevance(row[filter_csv_field], search_term)
                    else:
                        for field in CSV_FIELDS:
                            if field in row:
                                field_relevance = calculate_relevance(row[field], search_term)
                                relevance = max(relevance, field_relevance)
                    
                    if relevance > 0:
                        result = standardize_result(row, is_from_db=False)
                        result['relevance'] = relevance
                        results.append(result)
                else:
                    if not filter_csv_field or (filter_csv_field in row and row[filter_csv_field]):
                        result = standardize_result(row, is_from_db=False)
                        result['relevance'] = 100
                        results.append(result)

        if search_term:
            results = sorted(results, key=lambda x: x['relevance'], reverse=True)[:100]
        else:
            results = results[:100]

        return jsonify({"results": results, "count": len(results)})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/status', methods=['GET'])
def status():
    return jsonify({"status": "online"})

@app.route('/api/campos', methods=['GET'])
def get_campos():
    campos = [{"value": "", "label": "Todos os campos"}]
    
    for i, field in enumerate(DB_FIELDS):
        label = field.replace('_', ' ').title()
        campos.append({"value": field, "label": label})
    
    return jsonify({"campos": campos})

if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    debug = os.getenv('DEBUG', 'False').lower() == 'true'
    app.run(host='0.0.0.0', port=port, debug=debug)