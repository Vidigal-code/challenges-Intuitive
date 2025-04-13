import os
import mysql.connector
from .data_config import DataConfig


class DataImporter:
    def __init__(self):
        self.config = DataConfig()
        self.sources = self.config.get_sources()
        self.mysql = self.config.get_mysql_config()
        self.mysql["allow_local_infile"] = True
        self.mysql["use_pure"] = True
        self.conn = mysql.connector.connect(**self.mysql)
        self.cursor = self.conn.cursor()

    def _log_success(self, message):
        print(f"\033[92m[SUCCESS]\033[0m {message}")

    def _log_error(self, message):
        print(f"\033[91m[ERROR]\033[0m {message}")

    def _log_warning(self, message):
        print(f"\033[93m[WARNING]\033[0m {message}")

    def _log_info(self, message):
        print(f"[INFO] {message}")

    def create_tables(self):
        script_path = os.path.join("sqlfile", "create_tables.sql")

        try:
            if os.path.exists(script_path):
                self._log_info(f"Executando SQL do arquivo: {script_path}")
                with open(script_path, "r", encoding="utf-8") as f:
                    sql_script = f.read()

                for statement in sql_script.split(";"):
                    statement = statement.strip()
                    if statement:
                        self.cursor.execute(statement)

                self.conn.commit()
                self._log_success("Tabelas criadas via script.")
            else:
                self._log_warning(
                    "Script SQL não encontrado. Criando tabelas manualmente..."
                )

                self.cursor.execute(
                    """
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
    telefone VARCHAR(30), 
    fax VARCHAR(30),      
    endereco_eletronico VARCHAR(255),
    representante VARCHAR(255), 
    cargo_representante VARCHAR(255), 
    regiao_de_comercializacao VARCHAR(100), 
    data_registro_ans DATE
);
                """
                )

                self.cursor.execute(
                    """
   CREATE TABLE IF NOT EXISTS demonstracoes_contabeis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data DATE,
    reg_ans VARCHAR(20),
    cd_conta_contabil VARCHAR(20),
    descricao VARCHAR(255),
    vl_saldo_inicial DECIMAL(18,2),
    vl_saldo_final DECIMAL(18,2)
);

                """
                )

                self.conn.commit()
                self._log_success("Tabelas criadas manualmente.")
        except mysql.connector.Error as e:
            self._log_error(f"MySQL ao criar tabelas: {e}")
        except Exception as e:
            self._log_error(f"Ao criar tabelas: {e}")

    def importar_dados(self):
        for source in self.sources:
            tipo = source["type"]
            base_pasta = source["output"]
            encoding = source.get("encoding", "utf8mb4")
            filter = source.get("filter", [""])
            anos = source.get("ano", [])

            if not os.path.exists(base_pasta):
                self._log_error(f"Pasta não encontrada: {base_pasta}")
                continue

            if tipo == "demonstracoes":
                self.importar_arquivos(base_pasta, encoding, filter, anos, tipo)
            elif tipo == "operadoras":
                self.importar_arquivos(base_pasta, encoding, filter, [], tipo)

    def importar_arquivos(self, pasta, encoding, filters, anos, tipo):
        arquivos = []

        if tipo == "demonstracoes":
            for ano_folder in os.listdir(pasta):
                if anos and ano_folder not in anos:
                    continue

                ano_path = os.path.join(pasta, ano_folder)
                if os.path.isdir(ano_path):
                    arquivos += [
                        os.path.join(ano_path, f)
                        for f in os.listdir(ano_path)
                        if f.endswith(".csv")
                    ]
        else:
            arquivos = [
                os.path.join(pasta, f) for f in os.listdir(pasta) if f.endswith(".csv")
            ]

        for arquivo in arquivos:
            nome_arquivo = os.path.basename(arquivo).lower()

            if filters and filters != [""]:
                if not any(filter.lower() in nome_arquivo for filter in filters):
                    continue

            self._log_info(f"Importando via LOAD DATA LOCAL INFILE: {arquivo}")

            try:
                tabela = (
                    "operadoras" if tipo == "operadoras" else "demonstracoes_contabeis"
                )

                if tipo == "operadoras":
                    colunas = """(
                        registro_ans, cnpj, razao_social, nome_fantasia, modalidade,
                        logradouro, numero, complemento, bairro, cidade, uf,
                        cep, ddd, telefone, fax, endereco_eletronico,
                        representante, cargo_representante, regiao_de_comercializacao, data_registro_ans
                    )"""
                else:
                    colunas = """(
                        data, reg_ans, cd_conta_contabil, descricao,
                        vl_saldo_inicial, vl_saldo_final
                    )"""

                caminho_absoluto = os.path.abspath(arquivo).replace("\\", "\\\\")

                query = f"""
                    LOAD DATA LOCAL INFILE '{caminho_absoluto}'
                    INTO TABLE {tabela}
                    CHARACTER SET {encoding}
                    FIELDS TERMINATED BY ';'
                    ENCLOSED BY '"'
                    LINES TERMINATED BY '\\n'
                    IGNORE 1 LINES
                    {colunas};
                """

                self.cursor.execute("SET sql_mode='';")
                self.cursor.execute(query)
                self.conn.commit()
                self._log_success("Importação concluída.")

            except mysql.connector.Error as e:
                self._log_error(f"MySQL ao importar {arquivo}: {e}")
            except Exception as e:
                self._log_error(f"Ao processar {arquivo}: {e}")
