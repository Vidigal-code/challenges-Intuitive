import os
import re
import requests
from bs4 import BeautifulSoup

class PDFDownloader:
    def __init__(self, url, download_dir, anexos):
        self.url = url
        self.download_dir = download_dir
        self.anexos = anexos

    def download_anexos(self):
        BLUE = "\033[94m"
        GREEN = "\033[92m"
        RED = "\033[91m"
        RESET = "\033[0m"

        print(f"{BLUE}Acessando: {self.url}{RESET}")
        try:
            response = requests.get(self.url)
            response.raise_for_status()
            soup = BeautifulSoup(response.text, 'html.parser')

            links = soup.find_all('a', href=True)
            downloaded = []

            for anexo in self.anexos:
                nome = anexo["nome"]
                pattern = re.compile(anexo["regex"], re.IGNORECASE)

                for link in links:
                    href = link.get("href", "")
                    text = link.get_text()

                    if href.endswith(".pdf") and (pattern.search(text) or pattern.search(href)):
                        full_url = href
                        if not href.startswith("http"):
                            full_url = f"https://www.gov.br{href}" if href.startswith("/") else f"https://www.gov.br/{href}"

                        file_path = os.path.join(self.download_dir, f"{nome.replace(' ', '_')}.pdf")

                        if self.download_file(full_url, file_path):
                            downloaded.append(file_path)
                        break  
            return downloaded

        except Exception as e:
            print(f"{RED}Erro ao acessar o site: {e}{RESET}")
            return []

    def download_file(self, url, filepath):
        BLUE = "\033[94m"
        GREEN = "\033[92m"
        RED = "\033[91m"
        RESET = "\033[0m"

        try:
            print(f"{BLUE}Baixando: {url}{RESET}")
            response = requests.get(url, stream=True)
            response.raise_for_status()

            with open(filepath, 'wb') as f:
                for chunk in response.iter_content(chunk_size=8192):
                    f.write(chunk)

            print(f"{GREEN}Download concluído: {filepath}{RESET}")
            return True
        except Exception as e:
            print(f"{RED}Erro ao baixar {url}: {e}{RESET}")
            return False
