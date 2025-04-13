from scripts.data_download import DataDownloader
from scripts.data_import_all import DataImporter
from scripts.data_analysis import run_analytical_queries
from scripts.data_import_alternative import DataImportCSV


def print_info(message):
    print(f"\033[94m[INFO]\033[0m {message}")


def print_success(message):
    print(f"\033[92m[SUCCESS]\033[0m {message}")


def print_warning(message):
    print(f"\033[93m[WARNING]\033[0m {message}")


def print_error(message):
    print(f"\033[91m[ERROR]\033[0m {message}")


def main():
    print_info("Projeto ANS - Banco de Dados")
    print("Escolha uma opção:")
    print("1. Baixar arquivos")
    print("2. Criar tabelas e importar dados")
    print("3. Rodar queries analíticas")
    print("4. Criar/importar com script alternativo (INSERT linha a linha)")
    print("0. Sair")

    opcao = input("Digite a opção desejada: ")

    if opcao == '1':
        print_info("Baixando e extraindo arquivos...")
        DataDownloader().run()
        print_success("Arquivos baixados.")
    elif opcao == '2':
        print_info("Criando tabelas e importando dados...")
        importer = DataImporter()
        importer.create_tables()
        importer.importar_dados()
        print_success("Processo de criação e importação concluído.")
    elif opcao == '3':
        print_info("Executando análises...")
        run_analytical_queries()
        print_success("Análises finalizadas.")
    elif opcao == '4':
        print_info("Rodando script alternativo para importação detalhada...")
        DataImportCSV().run()
        print_success("Importação com script alternativo concluída.")
    elif opcao == '0':
        print_info("Saindo.")
    else:
        print_error("Opção inválida.")


if __name__ == "__main__":
    main()
