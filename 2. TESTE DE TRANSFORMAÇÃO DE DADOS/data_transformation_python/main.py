import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

from config.config_loader import ConfigLoader
from extractor.extractor import Extractor
from transformation.transformation import Transformation

GREEN = '\033[92m'
RED = '\033[91m'
RESET = '\033[0m'

def main():
    config_loader = ConfigLoader()
    configs = config_loader.config

    for index, config in enumerate(configs, start=1):
        print(f"\n\033[96m=== Processando configuração #{index} ===\033[0m")

        pdf_path = config["pdf_path"]
        csv_output = config["csv_output"]
        zip_output = config["zip_output"]
        replacements = config.get("replacements", {})

        try:
            print(f"{GREEN}[MAIN] Processando: {pdf_path}{RESET}")
            extractor = Extractor(pdf_path)
            df = extractor.extract_tables()

            transformer = Transformation(replacements)
            df_transformed = transformer.apply_transformations(df)

            transformer.save_to_csv(df_transformed, csv_output)
            transformer.zip_csv(csv_output, zip_output)

            print(f"{GREEN}[MAIN] Sucesso ao processar: {pdf_path}{RESET}\n")
        except Exception as e:
            print(f"{RED}[ERRO] Falha ao processar {pdf_path}: {e}{RESET}\n")

if __name__ == "__main__":
    main()

