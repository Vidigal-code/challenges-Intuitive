import zipfile
import os

class FileCompressor:
    def compress(self, files, output_zip):
        green = "\033[92m"
        red = "\033[91m"
        reset = "\033[0m"

        try:
            with zipfile.ZipFile(output_zip, 'w') as zipf:
                for file in files:
                    if os.path.exists(file):
                        arcname = os.path.basename(file)
                        zipf.write(file, arcname)
                        print(f"{green}Arquivo adicionado ao ZIP: {arcname}{reset}")
                    else:
                        print(f"{red}Arquivo não encontrado: {file}{reset}")
            print(f"{green}Compactação concluída: {output_zip}{reset}")
        except Exception as e:
            print(f"{red}Erro ao criar ZIP: {e}{reset}")
