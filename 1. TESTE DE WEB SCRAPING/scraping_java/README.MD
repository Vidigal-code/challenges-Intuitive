# Web Scraper - Java

## Descrição

Este projeto é um web scraper em Java que acessa o site da Agência Nacional de Saúde Suplementar (ANS) (ou outros sites configuráveis) para baixar automaticamente os anexos em PDF das páginas especificadas. Os arquivos baixados são então compactados em um único arquivo ZIP para facilitar o armazenamento e distribuição.

Agora com **suporte a múltiplos repositórios** de configuração, o scraper permite processar diferentes URLs com diferentes regras de extração em uma única execução.

---

## Estrutura do Projeto

O projeto está organizado em pacotes e composto pelas seguintes classes principais:

- **Pacote `com.scraping.java`**
    - `ScrapingMain.java`: Classe principal que coordena o fluxo de execução.
    - `LogsMain.java`: Utilitário para log com mensagens coloridas no console.

- **Pacote `com.scraping.java.config`**
    - `Config.java`: Responsável por carregar as configurações do projeto a partir de um arquivo JSON.
    - `Anexo.java`: Modelo que representa cada item (anexo) a ser buscado.
    - `Repository.java`: Novo modelo que representa cada repositório configurado (URL, diretório, anexos etc).

- **Pacote `com.scraping.java.directory`**
    - `DirectoryManager.java`: Classe que gerencia a criação de diretórios.
    - `PDFDownloader.java`: Classe que, utilizando o Jsoup, acessa a página da ANS, localiza e baixa os arquivos PDF.

- **Pacote `com.scraping.java.compressor`**
    - `FileCompressor.java`: Classe que compacta os arquivos baixados em um único arquivo ZIP.

- **Pacote de Testes**
    - `ScrapingTest.java`: Conjunto de testes unitários (utilizando JUnit Jupiter) que validam as principais funcionalidades do projeto.

---

## Requisitos

- Java 8 (ou superior)
- Maven

**Dependências principais:**
- [Jsoup](https://jsoup.org/)
- [Gson](https://github.com/google/gson)
- [JUnit Jupiter](https://junit.org/junit5/)

---

## Instalação

```bash
git clone https://github.com/Vidigal-code/challenges-Intuitive/tree/main/1.%20TESTE%20DE%20WEB%20SCRAPING/scraping_java.git
cd scraping_java
mvn clean package
```

---

## Configuração

### Arquivo `config/config.json`

A estrutura agora permite **múltiplos repositórios** em um único arquivo JSON:

```json
[
  {
    "url": "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos",
    "download_directory": "anexos_ans",
    "output_zip": "anexos_ans.zip",
    "anexos": [
      {
        "nome": "Anexo I",
        "regex": "anexo\\s*i"
      },
      {
        "nome": "Anexo II",
        "regex": "anexo\\s*ii"
      }
    ]
  },
  {
    "url": "https://www.gov.br/outro-orgao/documentos-importantes",
    "download_directory": "documentos_ministerio",
    "output_zip": "documentos_2025.zip",
    "anexos": [
      {
        "nome": "Relatório Anual",
        "regex": "relat[oó]rio\\s*anual"
      },
      {
        "nome": "Orçamento",
        "regex": "or[cç]amento\\s*2025"
      },
      {
        "nome": "Plano Estratégico",
        "regex": "plano\\s*estrat[eé]gico"
      }
    ]
  }
]
```

---

## Uso

Após configurar o arquivo JSON com os repositórios desejados, execute:

```bash
java -cp target/scraping_java-1.0-SNAPSHOT.jar com.scraping.java.ScrapingMain
```

Para cada repositório configurado, o programa irá:

1. Criar diretórios.
2. Acessar a URL correspondente.
3. Localizar os PDFs com base nas expressões regulares.
4. Baixar os arquivos.
5. Compactar os anexos em um arquivo `.zip`.

---

## Testes

Execute com:

```bash
mvn test
```

---

## Contribuição

Contribuições são bem-vindas via _issues_ ou _pull requests_!

---

## Licença

Este projeto é apenas para fins de aprendizado.



