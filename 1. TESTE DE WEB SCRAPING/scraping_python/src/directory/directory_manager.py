import os

class DirectoryManager:
    @staticmethod
    def create_directory(path):
        green = "\033[92m"
        reset = "\033[0m"

        if not os.path.exists(path):
            os.makedirs(path)
            print(f"{green}Diretório criado: {path}{reset}")
        else:
            print(f"{green}Diretório já existe: {path}{reset}")
