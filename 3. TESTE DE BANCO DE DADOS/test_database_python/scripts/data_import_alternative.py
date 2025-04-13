import os
import csv
import mysql.connector
from datetime import datetime
from .data_config import DataConfig


class DataImportCSV:
    def __init__(self):
        config = DataConfig()
        self.conn = mysql.connector.connect(**config.get_mysql_config())
        self.cursor = self.conn.cursor()
        self.paths = config.get_paths()

    def _parse_date(self, value):
        try:
            return datetime.strptime(value.strip(), "%Y-%m-%d").date()
        except Exception:
            return None

    def _parse_float(self, value):
        try:
            return float(value.replace(",", ".").strip())
        except (ValueError, AttributeError):
            return None

    def _log_info(self, msg):
        print(f"\033[94m[INFO]\033[0m {msg}")

    def _log_success(self, msg):
        print(f"\033[92m[SUCESSO]\033[0m {msg}")

    def _log_warning(self, msg):
        print(f"\033[93m[AVISO]\033[0m {msg}")

    def _log_error(self, msg):
        print(f"\033[91m[ERRO]\033[0m {msg}")

    def safe_get(self, row, key, default=None):
        return row.get(key, default).strip() if row.get(key) else default

    def close(self):
        self.cursor.close()
        self.conn.close()

    def criar_tabelas(self):
        self._log_info("Criando tabelas no banco de dados...")
        base_dir = os.path.dirname(os.path.dirname(__file__))
        sql_path = os.path.join(base_dir, "sqlfile", "create_tables.sql")

        if os.path.exists(sql_path):
            self._log_info(f"Script SQL encontrado: {sql_path}")
            with open(sql_path, "r", encoding="utf-8") as file:
                sql_script = file.read()
                for statement in sql_script.split(";"):
                    statement = statement.strip()
                    if statement:
                        try:
                            self.cursor.execute(statement)
                        except Exception as e:
                            self._log_warning(
                                f"Erro ao executar SQL: {statement} -> {e}"
                            )
        else:
            self._log_warning(
                "Arquivo SQL não encontrado. Criando tabelas manualmente."
            )
            self.cursor.execute(
                """ CREATE TABLE IF NOT EXISTS operadoras (
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
            )"""
            )
            self.cursor.execute(
                """ CREATE TABLE IF NOT EXISTS demonstracoes_contabeis (
                id INT AUTO_INCREMENT PRIMARY KEY,
                data DATE,
                reg_ans VARCHAR(20),
                cd_conta_contabil VARCHAR(20),
                descricao VARCHAR(255),
                vl_saldo_inicial DECIMAL(18,2),
                vl_saldo_final DECIMAL(18,2)
            )"""
            )
        self.conn.commit()
        self._log_success("Tabelas criadas com sucesso.")

    def importar_operadoras_csv(self, paths):
        for path in paths:
            self._log_info(f"Importando arquivo de operadoras: {path}")
            try:
                with open(path, newline="", encoding="utf-8") as csvfile:
                    reader = csv.DictReader(csvfile, delimiter=";")
                    reader.fieldnames = [
                        field.strip()
                        .lower()
                        .replace("ç", "c")
                        .replace("ã", "a")
                        .replace("á", "a")
                        .replace("é", "e")
                        .replace("í", "i")
                        .replace("ô", "o")
                        .replace("ú", "u")
                        for field in reader.fieldnames
                    ]
                    for row in reader:
                        try:
                            self.cursor.execute(
                                """INSERT INTO operadoras (
                                registro_ans, cnpj, razao_social, nome_fantasia,
                                modalidade, logradouro, numero, complemento, bairro,
                                cidade, uf, cep, ddd, telefone, fax,
                                endereco_eletronico, representante, cargo_representante,
                                regiao_de_comercializacao, data_registro_ans
                            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                                (
                                    self.safe_get(row, "registro_ans"),
                                    self.safe_get(row, "cnpj"),
                                    self.safe_get(row, "razao_social"),
                                    self.safe_get(row, "nome_fantasia"),
                                    self.safe_get(row, "modalidade"),
                                    self.safe_get(row, "logradouro"),
                                    self.safe_get(row, "numero"),
                                    self.safe_get(row, "complemento"),
                                    self.safe_get(row, "bairro"),
                                    self.safe_get(row, "cidade"),
                                    self.safe_get(row, "uf"),
                                    self.safe_get(row, "cep"),
                                    self.safe_get(row, "ddd"),
                                    self.safe_get(row, "telefone"),
                                    self.safe_get(row, "fax"),
                                    self.safe_get(row, "endereco_eletronico"),
                                    self.safe_get(row, "representante"),
                                    self.safe_get(row, "cargo_representante"),
                                    self.safe_get(row, "regiao_de_comercializacao"),
                                    self._parse_date(row.get("data_registro_ans")),
                                ),
                            )
                        except Exception as e:
                            self._log_error(f"Linha com erro: {row} -> {e}")
                self.conn.commit()
                self._log_success("Importação de operadoras concluída.")
            except FileNotFoundError:
                self._log_error(f"Arquivo não encontrado: {path}")
            except Exception as e:
                self._log_error(f"Erro inesperado: {e}")

    def importar_demonstracoes_csv(self, paths):
        for path in paths:
            self._log_info(f"Importando demonstrações contábeis do arquivo: {path}")
            try:
                with open(path, newline="", encoding="utf-8") as csvfile:
                    reader = csv.DictReader(csvfile, delimiter=";")
                    reader.fieldnames = [
                        field.strip().lower() for field in reader.fieldnames
                    ]
                    for row in reader:
                        try:
                            self.cursor.execute(
                                """INSERT INTO demonstracoes_contabeis (
                                data, reg_ans, cd_conta_contabil, descricao,
                                vl_saldo_inicial, vl_saldo_final
                            ) VALUES (%s, %s, %s, %s, %s, %s)""",
                                (
                                    self._parse_date(row.get("data")),
                                    self.safe_get(row, "reg_ans"),
                                    self.safe_get(row, "cd_conta_contabil"),
                                    self.safe_get(row, "descricao"),
                                    self._parse_float(row.get("vl_saldo_inicial")),
                                    self._parse_float(row.get("vl_saldo_final")),
                                ),
                            )
                        except Exception as e:
                            self._log_error(f"Linha com erro: {row} -> {e}")
                self.conn.commit()
                self._log_success("Importação de demonstrações contábeis concluída.")
            except FileNotFoundError:
                self._log_error(f"Arquivo não encontrado: {path}")
            except Exception as e:
                self._log_error(f"Erro inesperado: {e}")

    def run(self):
        self.criar_tabelas()

        operadoras_paths = self.paths.get("operadoras_csv", [])
        demonstracoes_paths = self.paths.get("demonstracoes_csv", [])

        if operadoras_paths:
            self.importar_operadoras_csv(operadoras_paths)
        else:
            print(
                "\033[93m[AVISO]\033[0m Nenhum caminho de arquivo CSV de operadoras fornecido."
            )

        if demonstracoes_paths:
            self.importar_demonstracoes_csv(demonstracoes_paths)
        else:
            print(
                "\033[93m[AVISO]\033[0m Nenhum caminho de arquivo CSV de demonstrações fornecido."
            )
