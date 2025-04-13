# 🕸️ scraping_python - Multidocumentos

## 📄 Descrição

Este projeto é um **web scraper em Python** que automatiza o processo de **download de arquivos PDF** a partir de páginas públicas, como a da [ANS](https://www.gov.br/ans/), e outros órgãos governamentais. Ele permite buscar vários anexos com base em **expressões regulares configuráveis**, salvá-los em diretórios específicos e compactá-los em arquivos `.zip` — tudo isso com um simples comando!

## 📁 Estrutura do Projeto

```
scraping_python/
│
├── config/
│   └── config.json                # Arquivo de configuração com múltiplas fontes
│
├── src/
│   ├── compressor/
│   │   └── file_compressor.py     # Classe para compactar arquivos em ZIP
│   ├── directory/
│   │   └── directory_manager.py   # Classe para criar diretórios
│   └── downloader/
│       └── pdf_downloader.py      # Classe para baixar os arquivos PDF
│
├── main.py                        # Script principal
├── requirements.txt               # Dependências do projeto
└── README.md                      # Documentação do projeto
```

---

## ⚙️ Requisitos

- Python **3.6+**
- Bibliotecas Python:
  - `requests`
  - `beautifulsoup4`

### Instalação

```bash
git clone https://github.com/seu-usuario/scraping_python.git
cd scraping_python
pip install -r requirements.txt
```

Ou manualmente:

```bash
pip install requests beautifulsoup4
```

---

## 🔧 Configuração

O scraper utiliza um arquivo de configuração JSON localizado em `config/config.json`. Este arquivo define **várias fontes de download**, seus diretórios e os anexos que devem ser buscados.

### Exemplo de configuração com múltiplas fontes:

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

> ⚡ **Dica**: Modifique o arquivo JSON para apontar para qualquer outro site com PDFs públicos e adapte as expressões regulares conforme necessário.

---

## 🚀 Uso

Basta executar o script principal para iniciar o processo de scraping:

```bash
python main.py
```

O programa irá:

1. Criar os diretórios de destino
2. Acessar cada URL configurada
3. Localizar os links para os anexos baseando-se nas regex configuradas
4. Baixar os PDFs encontrados
5. Compactar os arquivos em um ZIP no diretório correspondente

---

## 🔄 Flexibilidade

Este projeto é altamente reutilizável, com foco no conceito **"configuração sobre código"**. Ou seja, você pode reutilizar o mesmo código para:

- Baixar relatórios de diversos órgãos
- Manter arquivos organizados por tema
- Realizar auditorias automatizadas de documentos públicos

---

## ✏️ Personalização

Para adicionar novos anexos ou fontes:

1. Edite `config/config.json`
2. Insira uma nova entrada com:
   - `"url"`: a página que contém os anexos
   - `"download_directory"`: onde os arquivos serão salvos
   - `"output_zip"`: nome do arquivo ZIP final
   - `"anexos"`: lista de objetos com `nome` e `regex`

---

## 🤝 Contribuição

Pull requests são bem-vindos! Sinta-se à vontade para:

- Reportar bugs
- Sugerir melhorias
- Adicionar novos recursos

---

## 📜 Licença

Este projeto é livre para fins de **teste, estudo e aprendizado**. Use com responsabilidade ao acessar conteúdos de sites públicos.


