import json
import os

class ConfigLoader:
    GREEN = '\033[92m'
    RED = '\033[91m'
    RESET = '\033[0m'

    def __init__(self, config_path='config/config.json'):
        self.config_path = config_path
        self.config = self.load_config()

    def load_config(self):
        if not os.path.exists(self.config_path):
            print(f"{self.RED}Arquivo de configuração não encontrado: {self.config_path}{self.RESET}")
            raise FileNotFoundError(f"Arquivo de configuração não encontrado: {self.config_path}")
        with open(self.config_path, 'r', encoding='utf-8') as f:
            print(f"{self.GREEN}Configuração carregada com sucesso: {self.config_path}{self.RESET}")
            return json.load(f)
