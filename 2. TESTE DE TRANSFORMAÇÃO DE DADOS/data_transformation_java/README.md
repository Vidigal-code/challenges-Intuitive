Este projeto em **Java** tem como objetivo extrair dados estruturados de um arquivo PDF, aplicar transformações definidas via configuração e gerar arquivos CSV compactados em ZIP.

---

## 🛠️ Funcionalidades

- 📥 Leitura de arquivos PDF com múltiplas páginas contendo tabelas.
- 📊 Extração de todas as tabelas utilizando bibliotecas Java.
- 🔄 Aplicação de substituições com base em abreviações personalizadas.
- 💾 Salvamento dos dados em arquivo `.csv`.
- 📦 Compactação do `.csv` gerado em um arquivo `.zip`.
- ✅ Suporte a **várias configurações** via arquivo JSON.

---

## 📂 Estrutura do Projeto

```
data_transformation_java/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── transformation/
│   │               ├── extractor/
│   │               │   └── Extractor.java
│   │               ├── transformer/
│   │               │   └── Transformer.java
│   │               └── DataTransformation.java
│   └── test/
│       └── java/
│           └── com/
│               └── transformation/
│                   └── DataTransformationTest.java
├── config/
│   └── config.json
├── pom.xml
└── README.md

```

---

## ⚙️ Requisitos

- Java 17 ou superior
- Maven 3.8+
- Biblioteca externa: [Tabula](https://tabula.technology/) (via linha de comando para extração de tabelas de PDF)
- JSON de configuração com os caminhos e substituições

---

## 📦 Instalação

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/data_transformation_java.git
cd data_transformation_java
```

2. Compile o projeto com Maven:

```bash
mvn clean install
```

---

## 🚀 Execução

1. Configure o arquivo `config/config.json` com o seguinte formato:

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

2. Execute a aplicação:

```bash
mvn exec:java -Dexec.mainClass="com.transformation.Main"
```

> Certifique-se de que os diretórios de entrada e saída existem.

---

## ✅ Testes

Execute os testes com:

```bash
mvn test
```

---

## 📄 Licença

Este projeto é de uso livre para fins educacionais, acadêmicos ou demonstração.


