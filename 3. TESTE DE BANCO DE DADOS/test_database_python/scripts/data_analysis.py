import mysql.connector
import csv
import os
from pathlib import Path
from scripts.data_config import DataConfig


def run_analytical_queries():
    config = DataConfig().get_mysql_config()
    conn = mysql.connector.connect(**config)
    cursor = conn.cursor()

    print("\033[92mDigite os parâmetros ou pressione ENTER para usar o padrão.\033[0m")

    try:
        limit = int(input("Quantas operadoras mostrar (padrão: 10)? ") or 10)
    except ValueError:
        print("\033[91mEntrada inválida. Usando padrão: 10\033[0m")
        limit = 10

    print(
        "Exemplos de categorias: 'Provisão', 'EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR'"
    )
    categoria = (
        input("Categoria a buscar (pressione ENTER para usar o padrão):\n")
        or "EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR"
    )

    cursor.execute(
        """
        SELECT YEAR(data), QUARTER(data)
        FROM demonstracoes_contabeis
        ORDER BY data DESC
        LIMIT 1
    """
    )
    resultado = cursor.fetchone()
    if resultado:
        ano_trimestre, trimestre = resultado
    else:
        print(
            "\033[91mNenhum dado encontrado na tabela demonstracoes_contabeis.\033[0m"
        )
        cursor.close()
        conn.close()
        return

    print(
        f"\n\033[92mTop {limit} operadoras com maiores despesas em '{categoria}' no trimestre {trimestre} de {ano_trimestre}:\033[0m"
    )
    cursor.execute(
        """
        SELECT o.razao_social, SUM(dc.vl_saldo_final - dc.vl_saldo_inicial) AS total_despesa
        FROM demonstracoes_contabeis dc
        JOIN operadoras o ON o.registro_ans = dc.reg_ans
        WHERE dc.descricao LIKE %s
          AND YEAR(dc.data) = %s
          AND QUARTER(dc.data) = %s
        GROUP BY o.razao_social
        ORDER BY total_despesa DESC
        LIMIT %s
    """,
        (f"%{categoria}%", ano_trimestre, trimestre, limit),
    )
    trimestre_results = cursor.fetchall()
    for row in trimestre_results:
        print(f"{row[0]} - R$ {row[1]:,.2f}")

    print(
        f"\n\033[92mTop {limit} operadoras com maiores despesas em '{categoria}' no ano {ano_trimestre}:\033[0m"
    )
    cursor.execute(
        """
        SELECT o.razao_social, SUM(dc.vl_saldo_final - dc.vl_saldo_inicial) AS total_despesa
        FROM demonstracoes_contabeis dc
        JOIN operadoras o ON o.registro_ans = dc.reg_ans
        WHERE dc.descricao LIKE %s
          AND YEAR(dc.data) = %s
        GROUP BY o.razao_social
        ORDER BY total_despesa DESC
        LIMIT %s
    """,
        (f"%{categoria}%", ano_trimestre, limit),
    )
    ano_results = cursor.fetchall()
    for row in ano_results:
        print(f"{row[0]} - R$ {row[1]:,.2f}")

    print("\nDeseja exportar os resultados?")
    print("1 - Exportar resultados para .CSV")
    print("0 - Finalizar")
    escolha = input("Digite a opção desejada: ").strip()

    if escolha == "1":
        default_export_dir = Path(__file__).resolve().parents[1] / "export"

        caminho_export = input(
            f"Digite o diretório de exportação ou pressione ENTER para usar o padrão '{default_export_dir}':\n"
        ) or str(default_export_dir)
        os.makedirs(caminho_export, exist_ok=True)

        arquivo_trimestre = os.path.join(
            caminho_export, f"despesas_trimestre_{trimestre}_{ano_trimestre}.csv"
        )
        arquivo_ano = os.path.join(caminho_export, f"despesas_ano_{ano_trimestre}.csv")

        with open(arquivo_trimestre, mode="w", newline="", encoding="utf-8") as file:
            writer = csv.writer(file)
            writer.writerow(["Operadora", "Despesa (R$)"])
            for row in trimestre_results:
                writer.writerow([row[0], f"{row[1]:.2f}"])

        with open(arquivo_ano, mode="w", newline="", encoding="utf-8") as file:
            writer = csv.writer(file)
            writer.writerow(["Operadora", "Despesa (R$)"])
            for row in ano_results:
                writer.writerow([row[0], f"{row[1]:.2f}"])

        print(
            f"\033[92mArquivos exportados com sucesso para:\033[0m\n- {arquivo_trimestre}\n- {arquivo_ano}"
        )
    else:
        print("\033[92mFinalizado sem exportação.\033[0m")

    cursor.close()
    conn.close()
