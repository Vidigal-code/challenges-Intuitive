# 🧪 Teste de Transformação de Dados - Rol de Procedimentos

Este projeto em **Python** tem como objetivo extrair, transformar e salvar dados estruturados de um arquivo PDF contendo a tabela do **Rol de Procedimentos e Eventos em Saúde** (Anexo I).

---

## 🛠️ Funcionalidades

- 📥 Lê arquivos PDF com múltiplas páginas contendo tabelas.
- 📊 Extrai todas as tabelas estruturadas do PDF.
- 🔄 Realiza substituições com base em abreviações pré-definidas.
- 💾 Salva os dados em um arquivo `.csv`.
- 📦 Compacta o `.csv` gerado em um arquivo `.zip`.
- ✅ Suporta **vários arquivos PDF** definidos via JSON de configuração.

---

.
├── src/
│   ├── config/
│   │   └── config_loader.py       # Carregamento das configurações dos PDFs a processar
│   ├── extractor/
│   │   └── extractor.py           # Lógica de extração de dados dos PDFs
│   ├── transformation/
│   │   └── transformation.py      # Aplicação de transformações e salvamento em CSV/ZIP
├── main.py                        # Script principal do projeto
├── requirements.txt               # Dependências do projeto
└── README.md                      # Documentação do projeto

```

---

## ⚙️ Requisitos

- Python 3.8 ou superior
- Java instalado (obrigatório para uso do `tabula-py`)
- Biblioteca Tabula (utiliza o Java backend)

---

## 📦 Instalação

1. Clone o repositório:

```bash
git clone https://github.com/Vidigal-code/challenges-Intuitive/tree/main/2.%20TESTE%20DE%20TRANSFORMAÇÃO%20DE%20DADOS/data_transformation_python.git
cd teste-transformacao-dados
```

2. Crie um ambiente virtual (opcional, mas recomendado):

```bash
python -m venv venv
source venv/bin/activate  # ou .\venv\Scripts\activate no Windows
```

3. Instale as dependências:

```bash
pip install -r requirements.txt
```

---

## 🚀 Execução

Adicione seus caminhos e substituições no arquivo `config/config.json`. Exemplo:

```json
[
  {
    "pdf_path": "file/input/Anexo_I.pdf",
    "csv_output": "file/output/Rol_Procedimentos.csv",
    "zip_output": "file/output/Teste_Vidigal.zip",
    "replacements": {
      "OD": "Odontologia",
      "AMB": "Ambulatorial"
    }
  }
]
```

Depois execute:

```bash
python main.py
```

---

## ✅ Exemplo de Saída

- `Rol_Procedimentos.csv` será salvo em `file/output/`
- Um arquivo `Teste_Vidigal.zip` será criado contendo o CSV

---

## 🧩 Observações

- O projeto suporta **vários arquivos PDF**. Basta adicionar mais objetos no array do `config.json`.
- Requer que o Java esteja configurado corretamente no sistema para que o `tabula-py` funcione.

---

## 📄 Licença

Este projeto é apenas para fins de teste e aprendizado.
