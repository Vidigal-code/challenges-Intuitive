import pandas as pd
from tabula import read_pdf

class Extractor:
    GREEN = '\033[92m'
    RED = '\033[91m'
    RESET = '\033[0m'

    def __init__(self, pdf_path):
        self.pdf_path = pdf_path

    def extract_tables(self):
        print(f"{self.GREEN}[EXTRACTOR] Lendo PDF: {self.pdf_path}{self.RESET}")
        try:
            tables = read_pdf(self.pdf_path, pages='all', multiple_tables=True, lattice=True)
            if not tables:
                print(f"{self.RED}[EXTRACTOR] Nenhuma tabela encontrada no PDF.{self.RESET}")
                raise ValueError("Nenhuma tabela encontrada no PDF.")
            print(f"{self.GREEN}[EXTRACTOR] Tabelas extraídas com sucesso!{self.RESET}")
            return pd.concat(tables, ignore_index=True)
        except Exception as e:
            print(f"{self.RED}[EXTRACTOR] Erro ao extrair tabelas: {e}{self.RESET}")
            raise
