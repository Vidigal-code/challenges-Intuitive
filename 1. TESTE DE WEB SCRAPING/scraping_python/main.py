import json
import os
from src.directory.directory_manager import DirectoryManager
from src.downloader.pdf_downloader import PDFDownloader
from src.compressor.file_compressor import FileCompressor

GREEN = "\033[92m"
RED = "\033[91m"
RESET = "\033[0m"

def load_all_configs():
    config_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "config")
    config_path = os.path.join(config_dir, "config.json")

    DirectoryManager.create_directory(config_dir)

    if not os.path.exists(config_path):
        create_default_config(config_path)

    with open(config_path, "r", encoding="utf-8") as file:
        configs = json.load(file)

    if not isinstance(configs, list):
        raise ValueError("A configuração deve ser uma lista de objetos.")

    return configs

def process_config(config):
    url = config["url"]
    download_dir = config["download_directory"]
    output_zip = config["output_zip"]
    anexos = config["anexos"]

    DirectoryManager.create_directory(download_dir)

    pdf_downloader = PDFDownloader(url, download_dir, anexos)
    downloaded_files = pdf_downloader.download_anexos()

    if downloaded_files:
        compressor = FileCompressor()
        zip_path = os.path.join(download_dir, output_zip)
        compressor.compress(downloaded_files, zip_path)
    else:
        print(f"{RED}Nenhum anexo encontrado para download.{RESET}")

def main():
    try:
        configs = load_all_configs()
        for index, config in enumerate(configs, start=1):
            print(f"\n\033[96m=== Processando configuração #{index} ===\033[0m")
            process_config(config)
    except Exception as e:
        print(f"{RED}Erro na execução principal: {e}{RESET}")

if __name__ == "__main__":
    main()

