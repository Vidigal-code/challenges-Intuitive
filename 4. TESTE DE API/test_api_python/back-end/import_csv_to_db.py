import os
import csv
import pymysql
from dotenv import load_dotenv
from datetime import datetime

load_dotenv()

db_config = {
    "host": os.getenv("DB_HOST"),
    "user": os.getenv("DB_USER"),
    "password": os.getenv("DB_PASSWORD"),
    "database": os.getenv("DB_NAME"),
    "charset": "utf8mb4",
    "cursorclass": pymysql.cursors.DictCursor,
}

CSV_TO_DB_MAPPING = {
    "Registro_ANS": "registro_ans",
    "CNPJ": "cnpj",
    "Razao_Social": "razao_social",
    "Nome_Fantasia": "nome_fantasia",
    "Modalidade": "modalidade",
    "Logradouro": "logradouro",
    "Numero": "numero",
    "Complemento": "complemento",
    "Bairro": "bairro",
    "Cidade": "cidade",
    "UF": "uf",
    "CEP": "cep",
    "DDD": "ddd",
    "Telefone": "telefone",
    "Fax": "fax",
    "Endereco_eletronico": "endereco_eletronico",
    "Representante": "representante",
    "Cargo_Representante": "cargo_representante",
    "Regiao_de_Comercializacao": "regiao_de_comercializacao",
    "Data_Registro_ANS": "data_registro_ans",
}


def format_date(value: str):
    try:
        return datetime.strptime(value, "%Y-%m-%d").date()
    except ValueError:
        try:
            return datetime.strptime(value, "%d/%m/%Y").date()
        except ValueError:
            return None


def import_csv_to_db():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()

        cursor.execute("SHOW TABLES LIKE 'operadoras'")
        if not cursor.fetchone():
            print("Criando tabela operadoras...")
            create_table_sql = """
            CREATE TABLE `operadoras` (
  `id` int NOT NULL AUTO_INCREMENT,
  `registro_ans` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cnpj` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `razao_social` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nome_fantasia` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `modalidade` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `logradouro` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `complemento` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bairro` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cidade` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uf` char(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cep` varchar(9) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ddd` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fax` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `endereco_eletronico` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `representante` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cargo_representante` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `regiao_de_comercializacao` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `data_registro_ans` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """
            cursor.execute(create_table_sql)
            conn.commit()
            print("Tabela criada com sucesso!")

        csv_file_name = os.getenv("CSV_FILE", "Relatorio_cadop") + ".csv"
        base_dir = os.path.abspath(os.path.dirname(__file__))
        csv_path = os.path.join(base_dir, csv_file_name)

        if not os.path.exists(csv_path):
            print(f"Arquivo CSV não encontrado: {csv_path}")
            return

        cursor.execute("SELECT COUNT(*) as total FROM operadoras")
        if cursor.fetchone()["total"] > 0:
            print("A tabela já contém dados. Deseja apagar antes de importar? (s/n)")
            if input().lower() == "s":
                cursor.execute("TRUNCATE TABLE operadoras")
                conn.commit()
                print("Tabela limpa com sucesso!")
            else:
                print("Importação cancelada.")
                return

        with open(csv_path, "r", encoding="utf-8") as file:
            sample = file.read(4096)
            file.seek(0)
            delimiter = csv.Sniffer().sniff(sample).delimiter
            print(f"Delimitador detectado: '{delimiter}'")

        with open(csv_path, "r", encoding="utf-8") as file:
            reader = csv.DictReader(file, delimiter=delimiter)
            headers = reader.fieldnames
            print("Cabeçalhos detectados:", headers)

            insert_sql = """
            INSERT INTO operadoras (
                registro_ans, cnpj, razao_social, nome_fantasia, modalidade,
                logradouro, numero, complemento, bairro, cidade, uf, cep,
                ddd, telefone, fax, endereco_eletronico, representante,
                cargo_representante, regiao_de_comercializacao, data_registro_ans
            ) VALUES (
                %(registro_ans)s, %(cnpj)s, %(razao_social)s, %(nome_fantasia)s, %(modalidade)s,
                %(logradouro)s, %(numero)s, %(complemento)s, %(bairro)s, %(cidade)s, %(uf)s, %(cep)s,
                %(ddd)s, %(telefone)s, %(fax)s, %(endereco_eletronico)s, %(representante)s,
                %(cargo_representante)s, %(regiao_de_comercializacao)s, %(data_registro_ans)s
            )
            """

            count = 0
            for row in reader:
                data = {}
                for csv_field, db_field in CSV_TO_DB_MAPPING.items():
                    raw_value = row.get(csv_field, "").strip()
                    if db_field == "data_registro_ans":
                        data[db_field] = format_date(raw_value)
                    else:
                        data[db_field] = raw_value or None

                cursor.execute(insert_sql, data)
                count += 1
                if count % 100 == 0:
                    print(f"{count} registros inseridos...")

            conn.commit()
            print(f"Importação finalizada! Total de registros inseridos: {count}")

    except Exception as e:
        print("Erro durante a importação:", e)
    finally:
        if "conn" in locals():
            conn.close()
            print("Conexão com o banco encerrada.")


if __name__ == "__main__":
    import_csv_to_db()
