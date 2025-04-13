import os
import requests
from zipfile import ZipFile
from scripts.data_config import DataConfig


class DataDownloader:
    def __init__(self):
        self.config = DataConfig().get_sources()

    def download_file(self, url, dest_path):
        os.makedirs(os.path.dirname(dest_path), exist_ok=True)
        if os.path.exists(dest_path):
            print(f"\033[92mJá existe: {dest_path}\033[0m")
            return dest_path

        print(f"\033[92mBaixando: {url}\033[0m")
        response = requests.get(url)
        response.raise_for_status()

        with open(dest_path, "wb") as f:
            f.write(response.content)
        return dest_path

    def extract_zip(self, zip_path, extract_to):
        with ZipFile(zip_path, "r") as zip_ref:
            zip_ref.extractall(extract_to)
        print(f"\033[92mExtraído: {zip_path}\033[0m")

    def download_demonst(self, filter):
        base = next(x for x in self.config if x["type"] == "demonstracoes")
        base_url = base["source"]
        anos = base.get("ano", ["2024"])
        output_base = base["output"]

        if filter and filter != [""]:
            ano_trimestres = filter
        else:
            ano_trimestres = []
            for ano in anos:
                ano_trimestres.extend(self.get_all_available_from_ftp(base_url, ano))

        for item in ano_trimestres:
            ano = item[-4:]
            zip_name = f"{item}.zip"
            url = f"{base_url}{ano}/{zip_name}"
            ano_output = os.path.join(output_base, ano)
            dest_path = os.path.join(ano_output, zip_name)

            try:
                zip_path = self.download_file(url, dest_path)
                self.extract_zip(zip_path, ano_output)
            except Exception as e:
                print(f"\033[91mErro ao baixar {zip_name}: {e}\033[0m")

    def download_operadoras(self, filter):
        base = next(x for x in self.config if x["type"] == "operadoras")
        base_url = base["source"]
        output_dir = base["output"]

        arquivos = (
            filter if filter and filter != [""] else self.get_all_csv_from_ftp(base_url)
        )

        for item in arquivos:
            file_name = f"{item}.csv"
            url = base_url + file_name
            dest_path = os.path.join(output_dir, file_name)

            try:
                self.download_file(url, dest_path)
            except Exception as e:
                print(f"\033[91mErro ao baixar {file_name}: {e}\033[0m")

    def get_all_available_from_ftp(self, url, ano):
        return [f"{t}T{ano}" for t in range(1, 5)]

    def get_all_csv_from_ftp(self, url):
        return ["Relatorio_cadop"]

    def run(self):
        for base in self.config:
            tipo = base["type"]
            filter = base.get("filter", [""])

            if tipo == "demonstracoes":
                print("\033[92mBaixando demonstrações contábeis...\033[0m")
                self.download_demonst(filter)
            elif tipo == "operadoras":
                print("\n\033[92mBaixando arquivos de operadoras...\033[0m")
                self.download_operadoras(filter)
