# 🧪 Teste de Transformação de Dados - Rol de Procedimentos

Projeto em **Python** para **extração, transformação e salvamento** de dados estruturados a partir de arquivos PDF contendo a tabela do **Rol de Procedimentos e Eventos em Saúde** (Anexo I).

---

## 🛠️ Funcionalidades

- **📥 Leitura de arquivos PDF:** Suporta PDFs com múltiplas páginas contendo tabelas.
- **📊 Extração automática:** Detecta e extrai todas as tabelas estruturadas.
- **🔄 Substituição de abreviações:** Realiza substituições de termos conforme definido.
- **💾 Salvamento em CSV:** Armazena os dados extraídos em um arquivo `.csv`.
- **📦 Compactação:** Gera um arquivo `.zip` contendo o CSV.
- **✅ Vários arquivos:** Processa múltiplos PDFs com configurações definidas via JSON.

---

## 📁 Estrutura do Projeto

```
├── src/
│   ├── config/
│   │   └── config_loader.py       # Carrega as configurações dos PDFs a processar
│   ├── extractor/
│   │   └── extractor.py           # Lógica de extração dos dados
│   ├── transformation/
│   │   └── transformation.py      # Aplicação de transformações e salvamento em CSV/ZIP
├── main.py                        # Script principal do projeto
├── requirements.txt               # Dependências
└── README.md                      # Documentação do projeto
```

---

## ⚙️ Requisitos

- **Python 3.8 ou superior**
- **Java** instalado (necessário para o `tabula-py`)
- Biblioteca `tabula-py` (usa backend Java)

---

## 📦 Instalação

1. **Clone o repositório completo:**  
   *Observação:* O link abaixo clona o repositório inteiro. Em seguida, navegue até a pasta do projeto.

   ```bash
   git clone https://github.com/Vidigal-code/challenges-Intuitive/blob/main/2.%20TESTE%20DE%20TRANSFORMAÇÃO%20DE%20DADOS/data_transformation_python.git
   cd "data_transformation_python"
   ```

2. **Crie um ambiente virtual (opcional, mas recomendado):**

   ```bash
   python -m venv venv
   ```

   - No Linux/macOS:

     ```bash
     source venv/bin/activate
     ```

   - No Windows:

     ```bash
     .\venv\Scripts\activate
     ```

3. **Instale as dependências:**

   ```bash
   pip install -r requirements.txt
   ```

---

## 🚀 Execução

1. **Configure o arquivo `config/config.json`:**  
   Adicione os caminhos dos PDFs e as substituições desejadas. Exemplo:

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

2. **Execute o script principal:**

   ```bash
   python main.py
   ```

---

## ✅ Exemplo de Saída

- O arquivo `Rol_Procedimentos.csv` será salvo em `file/output/`
- Um arquivo compactado `Teste_Vidigal.zip` será gerado com o CSV

---

## 🧩 Observações

- **Múltiplos PDFs:** O projeto suporta a execução em vários arquivos. Basta incluir mais objetos no array do `config.json`.
- **Java:** Certifique-se que o Java esteja corretamente instalado e configurado para a funcionalidade do `tabula-py`.

---

## 📄 Licença

Este projeto destina-se exclusivamente a fins de **teste e aprendizado**.

