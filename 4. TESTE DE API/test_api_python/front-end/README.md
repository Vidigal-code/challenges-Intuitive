# 📄 Documentação do Front-end - Sistema de Busca de Operadoras de Saúde

## 🔍 Visão Geral

O front-end do sistema foi desenvolvido com **Vue.js** com foco em uma interface **amigável**, **responsiva** e de fácil integração com a API backend. O sistema possibilita a busca e filtragem de operadoras de saúde de forma eficiente.

---

## 🎯 Funcionalidades Principais

### 1. Interface de Busca Avançada
- Campo de busca por termo (nome, registro etc.).
- Seleção de filtros por campos específicos.
- Escolha da **fonte de dados**: `banco de dados` ou `arquivo CSV`.

### 2. Exibição de Resultados
- Tabela estruturada com os resultados da busca.
- Informações detalhadas por operadora.

---

## ⚙️ Configuração de Ambiente

Antes de executar o front-end localmente, é necessário criar um arquivo `.env` na raiz do projeto com a seguinte variável:

```env
VUE_APP_API_BASE_URL=http://localhost:5000/api
```

Essa variável define a URL base da API utilizada para realizar as buscas de operadoras de saúde.

