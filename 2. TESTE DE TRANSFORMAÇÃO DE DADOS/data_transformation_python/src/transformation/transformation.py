import pandas as pd
import zipfile
import os

class Transformation:
    GREEN = '\033[92m'
    RED = '\033[91m'
    RESET = '\033[0m'

    def __init__(self, replacements):
        self.replacements = replacements

    def apply_transformations(self, df):
        print(f"{self.GREEN}[TRANSFORMER] Aplicando substituições...{self.RESET}")
        try:
            for column in df.columns:
                if df[column].dtype == object:
                    df[column] = df[column].replace(self.replacements, regex=True)
            print(f"{self.GREEN}[TRANSFORMER] Substituições aplicadas com sucesso!{self.RESET}")
        except Exception as e:
            print(f"{self.RED}[TRANSFORMER] Erro ao aplicar substituições: {e}{self.RESET}")
            raise
        return df

    def save_to_csv(self, df, output_csv):
        try:
            df.to_csv(output_csv, index=False, encoding='utf-8-sig')
            print(f"{self.GREEN}[TRANSFORMER] CSV salvo em: {output_csv}{self.RESET}")
        except Exception as e:
            print(f"{self.RED}[TRANSFORMER] Erro ao salvar CSV: {e}{self.RESET}")
            raise

    def zip_csv(self, csv_path, zip_path):
        try:
            with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
                zipf.write(csv_path, arcname=os.path.basename(csv_path))
            os.remove(csv_path)
            print(f"{self.GREEN}[TRANSFORMER] ZIP criado em: {zip_path}{self.RESET}")
        except Exception as e:
            print(f"{self.RED}[TRANSFORMER] Erro ao criar ZIP: {e}{self.RESET}")
            raise
