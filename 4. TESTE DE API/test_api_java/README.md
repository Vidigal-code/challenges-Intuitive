# Documentação da API do Controlador de Operadoras

Este documento fornece uma explicação abrangente da API OperadoraController, incluindo sua funcionalidade, estrutura e detalhes de implementação.

## Sumário
- [Visão Geral](#visão-geral)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Implementação do Controlador](#implementação-do-controlador)
- [Componentes Principais](#componentes-principais)
- [Endpoints da API](#endpoints-da-api)
- [Gerenciamento de Dados](#gerenciamento-de-dados)
- [Algoritmo de Busca](#algoritmo-de-busca)
- [Configuração](#configuração)
- [Testes](#testes)

## Visão Geral

A aplicação é uma API Spring Boot que fornece funcionalidade de busca para operadoras de saúde no Brasil. Ela permite aos usuários pesquisar operadoras a partir de um banco de dados ou de um arquivo CSV, com opções para filtrar por campos específicos e termos de busca.

Os dados parecem estar relacionados à ANS (Agência Nacional de Saúde Suplementar), que é a agência reguladora brasileira para planos e seguros de saúde privados.

## Estrutura do Projeto

O projeto consiste em três arquivos principais:
1. `OperadoraController.java` - O controlador principal da API
2. `OperadoraControllerTest.java` - Testes JUnit para o controlador
3. `application.properties` - Propriedades de configuração para a aplicação Spring

## Implementação do Controlador

A classe `OperadoraController` serve como um controlador REST manipulando requisições da API relacionadas às operadoras de saúde. Ela é anotada com `@RestController` e mapeia para o caminho base `/api`.

### Componentes Principais

#### Definições de Campos
```java
private static final List<String> DB_FIELDS = Arrays.asList(...);
private static final List<String> CSV_FIELDS = Arrays.asList(...);
private static final List<String> OUTPUT_FIELDS = Arrays.asList(...);
```

Estas listas definem os nomes dos campos usados em:
- Colunas do banco de dados (`DB_FIELDS`)
- Cabeçalhos do arquivo CSV (`CSV_FIELDS`)
- Campos de saída padronizados (`OUTPUT_FIELDS`)

Os campos representam propriedades das operadoras de saúde como número de registro, nome da empresa, endereço, etc.

#### Métodos Utilitários

A classe contém vários métodos utilitários:

1. `calculateRelevance(String text, String searchTerm)` - Calcula uma pontuação de relevância (0-100) entre o texto e um termo de busca, com pontuações mais altas para correspondências exatas
2. `standardizeResult(Map<String, Object> row, boolean isFromDb)` - Converte resultados do banco de dados ou CSV para um formato de saída padronizado

### Endpoints da API

O controlador define quatro endpoints principais:

#### 1. `/api/status` - Endpoint de Verificação de Saúde
Retorna uma mensagem de status simples para indicar que a API está em execução.

```java
@GetMapping("/status")
public ResponseEntity<?> status() {
    Map<String, String> status = new HashMap<>();
    status.put("status", "online");
    return ResponseEntity.ok(status);
}
```

#### 2. `/api/campos` - Endpoint de Informações de Campos
Retorna uma lista de campos disponíveis para filtragem, com seus rótulos e valores.

```java
@GetMapping("/campos")
public ResponseEntity<?> getCampos() {
    // Cria uma lista de definições de campos para a UI
    // ...
}
```

#### 3. `/api/operadoras/db` - Endpoint de Busca no Banco de Dados
Pesquisa operadoras no banco de dados com base em uma string de consulta e filtro de campo opcional.

```java
@GetMapping("/operadoras/db")
public ResponseEntity<?> searchOperadorasDb(@RequestParam(required = false) String q,
                                           @RequestParam(required = false) String filter) {
    // Valida parâmetros de entrada
    // Constrói consulta SQL com filtros
    // Executa consulta usando JdbcTemplate
    // Calcula pontuações de relevância
    // Retorna resultados ordenados
}
```

#### 4. `/api/operadoras/csv` - Endpoint de Busca no Arquivo CSV
Pesquisa operadoras em um arquivo CSV com base em uma string de consulta e filtro de campo opcional.

```java
@GetMapping("/operadoras/csv")
public ResponseEntity<?> searchOperadorasCsv(@RequestParam(required = false) String q,
                                            @RequestParam(required = false) String filter) {
    // Valida parâmetros de entrada
    // Lê arquivo CSV
    // Filtra resultados com base nos critérios
    // Calcula pontuações de relevância
    // Retorna resultados ordenados
}
```

## Gerenciamento de Dados

A aplicação suporta duas fontes de dados:

### 1. Banco de Dados
- Usa o `JdbcTemplate` do Spring para operações de banco de dados
- Geração dinâmica de consultas SQL baseadas em termos de busca e filtros
- Resultados são limitados a 100 registros
- Configuração MySQL em `application.properties`

### 2. Arquivo CSV
- Lê de um arquivo CSV especificado na propriedade `csv.file.path`
- Analisa registros CSV e aplica filtragem com base nos critérios de busca
- Resultados são limitados a 100 registros

## Algoritmo de Busca

A funcionalidade de busca usa uma abordagem baseada em relevância:

1. Uma pontuação de relevância (0-100) é calculada para cada registro com base em quão bem ele corresponde ao termo de busca
2. Correspondências perfeitas recebem uma pontuação de 100
3. Correspondências de contém recebem uma pontuação de 80
4. Correspondências parciais de palavras recebem pontuações mais baixas
5. Os resultados são ordenados por relevância (mais alta primeiro)

```java
private int calculateRelevance(String text, String searchTerm) {
    // Lógica para pontuação de relevância do texto para o termo de busca
    // ...
}
```

## Configuração

### Configuração CORS
A aplicação inclui uma configuração CORS que permite requisições de origem cruzada de qualquer origem:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
```

### Propriedades da Aplicação
```
spring.datasource.url=jdbc:mysql://localhost:3306/ans_test_db
spring.datasource.username=root
spring.datasource.password=Kzlui_Xga4wO
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
server.port=5000

csv.file.path=src/main/resources/Relatorio_cadop.csv
```

## Testes

A aplicação inclui testes JUnit abrangentes em `OperadoraControllerTest`:

### Configuração de Teste
- Cria um mock de `JdbcTemplate`
- Gera um arquivo CSV temporário para testes
- Injeta dependências usando `ReflectionTestUtils` do Spring

### Casos de Teste
1. `testStatusEndpoint` - Verifica se o endpoint de status retorna "online"
2. `testGetCampos` - Verifica se o endpoint de campos retorna a lista correta de campos
3. `testSearchOperadorasDbWithEmptyParams` - Verifica o tratamento de erros para parâmetros ausentes
4. `testSearchOperadorasDbWithShortQuery` - Verifica o tratamento de erros para consultas muito curtas
5. `testSearchOperadorasCsvWithValidQuery` - Verifica se a busca CSV retorna resultados para consultas válidas
6. `testSearchOperadorasCsvWithFilter` - Verifica a busca CSV com filtragem de campo

### Abordagem de Testes
- Usa Mockito para simular dependências
- Testa diretamente endpoints do controlador
- Verifica casos de sucesso e erro
- Verifica códigos de resposta e conteúdo do corpo


### Endpoints de Busca

- **Base URL**: `http://localhost:5000/api`
- **Modos**:
    - Banco de Dados: `/operadoras/db`
    - CSV: `/operadoras/csv`

#### Parâmetros:
- `q`: Termo de busca
- `campo` (opcional): Filtro de campo específico

#### Exemplos:

- **Com Filtro**:
    - Banco de Dados:  
      `http://localhost:5000/api/operadoras/db?q=12345678000195&campo=cnpj`

    - CSV:  
      `http://localhost:5000/api/operadoras/csv?q=UNIMED&campo=razao_social`

- **Sem Filtro**:
    - Banco de Dados:  
      `http://localhost:5000/api/operadoras/db?q=UNIMED`

    - CSV:  
      `http://localhost:5000/api/operadoras/csv?q=UNIMED`




# Documentação do Código

Este documento descreve a estrutura e o funcionamento de uma aplicação React TypeScript (TSX) que permite buscar operadoras de saúde com base em diferentes filtros. A aplicação é estilizada com **Tailwind CSS** e inclui funcionalidades como alternância entre temas claro e escuro, paginação, busca e exibição de resultados tabulares.

---

## ⚙️ Configuração de Ambiente

Antes de executar o front-end localmente, é necessário criar um arquivo `.env` na raiz do projeto com a seguinte variável:

```env
VITE_APP_API_BASE_URLL=http://localhost:5000/api
```

Essa variável define a URL base da API utilizada para realizar as buscas de operadoras de saúde.

---


## Estrutura do Código

### 1. **Estado da Aplicação**
A aplicação utiliza o hook `useState` para gerenciar o estado de várias variáveis:

- **`searchTerm`**: Termo de busca inserido pelo usuário.
- **`searchMode`**: Modo de busca (`"db"` para banco de dados ou `"csv"` para arquivo CSV).
- **`filterField`**: Campo selecionado para filtrar a busca.
- **`lastSearch`**: Último termo de busca realizado.
- **`results`**: Lista de resultados retornados pela API.
- **`loading`**: Indica se a aplicação está carregando os resultados.
- **`error`**: Mensagem de erro exibida caso ocorra um problema na busca.
- **`apiStatus`**: Status da API (`"online"` ou `"offline"`).
- **`currentPage`**: Página atual da paginação.
- **`pageSize`**: Número de resultados por página.

```tsx
const [searchTerm, setSearchTerm] = useState<string>("");
const [searchMode, setSearchMode] = useState<string>("db");
const [filterField, setFilterField] = useState<string>("");
const [lastSearch, setLastSearch] = useState<string>("");
const [results, setResults] = useState<Operadora[]>([]);
const [loading, setLoading] = useState<boolean>(false);
const [error, setError] = useState<string | null>(null);
const [apiStatus, setApiStatus] = useState<string>("verificando...");
const [currentPage, setCurrentPage] = useState<number>(1);
const [pageSize] = useState<number>(10);
```

---

### 2. **Interface `Operadora`**
Define a estrutura dos dados retornados pela API. Cada operadora possui informações como CNPJ, razão social, endereço, telefone, etc.

```tsx
interface Operadora {
    relevance: number;
    registro_ans: string;
    cnpj: string;
    razao_social: string;
    nome_fantasia: string;
    modalidade: string;
    logradouro: string;
    numero: string;
    complemento: string;
    bairro: string;
    cidade: string;
    uf: string;
    cep: string;
    ddd: string;
    telefone: string;
    fax: string;
    endereco_eletronico: string;
    representante: string;
    cargo_representante: string;
    regiao_de_comercializacao: string;
    data_registro_ans: string;
}
```

---

### 3. **Campos de Busca**
A lista `camposDeBusca` define os campos disponíveis para filtragem. Inclui opções como "Registro ANS", "CNPJ", "Razão Social", etc.

```tsx
const camposDeBusca = [
    { value: "", label: "Todos os campos" },
    { value: "registro_ans", label: "Registro ANS" },
    { value: "cnpj", label: "CNPJ" },
    { value: "razao_social", label: "Razão Social" },
    // ... outros campos
];
```

---

### 4. **Funções Principais**

#### 4.1. **Verificação de Status da API**
A função `checkApiStatus` verifica se a API está online ou offline.

```tsx
const checkApiStatus = async () => {
    try {
        const response = await axios.get(`${apiBaseUrl}/status`);
        setApiStatus(response.data?.status === "online" ? "online" : "offline");
    } catch (error) {
        console.error("Erro ao verificar status da API:", error);
        setApiStatus("offline");
    }
};
```

#### 4.2. **Busca de Operadoras**
A função `search` realiza a busca de operadoras com base no termo de busca (`searchTerm`) e no campo selecionado (`filterField`). Os resultados são paginados e exibidos em uma tabela.

```tsx
const search = async () => {
    if ((!searchTerm || searchTerm.length < 2) && !filterField) {
        setError("Por favor, digite pelo menos 2 caracteres para buscar ou selecione um filtro");
        return;
    }

    setLoading(true);
    setError(null);
    setLastSearch(searchTerm);
    setCurrentPage(1);
    setResults([]);

    try {
        const endpoint = searchMode === "db" ? `${apiBaseUrl}/operadoras/db` : `${apiBaseUrl}/operadoras/csv`;
        const response = await axios.get(endpoint, { params: { q: searchTerm, filter: filterField } });

        if (response.data && Array.isArray(response.data.results)) {
            setResults(response.data.results);
        } else {
            setResults([]);
            setError("Formato de resposta inválido da API");
        }
    } catch (error) {
        console.error("Erro ao buscar:", error);
        setError("Erro ao buscar operadoras. Verifique se o servidor está online.");
        setResults([]);
    } finally {
        setLoading(false);
    }
};
```

#### 4.3. **Formatação de Data**
A função `formatDate` converte datas no formato ISO para o formato brasileiro (`DD/MM/YYYY`).

```tsx
const formatDate = (dateString: string): string => {
    if (!dateString) return "";
    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return dateString;
        const day = date.getDate().toString().padStart(2, "0");
        const month = (date.getMonth() + 1).toString().padStart(2, "0");
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
    } catch (e) {
        console.error("Error formatting date:", e);
        return dateString;
    }
};
```

---

### 5. **Interface do Usuário**

#### 5.1. **Cabeçalho**
O cabeçalho contém o título da aplicação e dois elementos:
- **Status da API**: Exibe se a API está online ou offline.
- **Botão de Alternar Tema**: Alterna entre os modos claro e escuro.

```tsx
<header>
    <h1 className="text-2xl font-bold">Busca de Operadoras de Saúde</h1>
    <div>
        <p className={`status-indicator ${apiStatus === "online" ? "online" : "offline"}`}>
            API Status: {apiStatus}
        </p>
        <button id="theme-toggle">Alternar Tema</button>
    </div>
</header>
```

#### 5.2. **Formulário de Busca**
Inclui:
- Um campo de seleção para escolher o campo de filtro.
- Um campo de texto para inserir o termo de busca.
- Botões para alternar entre busca no banco de dados ou CSV.

```tsx
<div className="search-form">
    <select value={filterField} onChange={(e) => setFilterField(e.target.value)}>
        {camposDeBusca.map((campo) => (
            <option key={campo.value} value={campo.value}>
                {campo.label}
            </option>
        ))}
    </select>
    <input
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        onKeyPress={(e) => e.key === "Enter" && search()}
        placeholder={searchPlaceholder}
    />
    <button onClick={search}>Buscar</button>
</div>
```

#### 5.3. **Tabela de Resultados**
Os resultados são exibidos em uma tabela com paginação. Cada linha representa uma operadora, e as colunas incluem informações como CNPJ, razão social, endereço, etc.

```tsx
<table>
    <thead>
        <tr>
            <th>Relevância</th>
            <th>Registro ANS</th>
            <th>CNPJ</th>
            {/* ... outras colunas */}
        </tr>
    </thead>
    <tbody>
        {paginatedResults.map((operadora, index) => (
            <tr key={index}>
                <td>{Math.round(operadora.relevance)}%</td>
                <td>{operadora.registro_ans}</td>
                <td>{operadora.cnpj}</td>
                {/* ... outras células */}
            </tr>
        ))}
    </tbody>
</table>
```

---

### 6. **Estilização com Tailwind CSS**
A aplicação utiliza **Tailwind CSS** para estilização. As classes são responsivas e adaptam-se automaticamente a diferentes tamanhos de tela. Por exemplo:

- **Modo Escuro**: Classes como `dark:bg-gray-900` e `dark:text-gray-200` ajustam as cores para o modo escuro.
- **Transições**: Classes como `transition duration-300` adicionam animações suaves.

---

### 7. **Funcionalidade de Alternância de Tema**
O botão "Alternar Tema" alterna entre os modos claro e escuro. O tema selecionado é salvo no `localStorage` para persistência.

```tsx
useEffect(() => {
    const currentTheme = localStorage.getItem("theme");
    if (currentTheme === "dark") {
        document.documentElement.classList.add("dark");
    }
}, []);

const toggleTheme = () => {
    const isDark = document.documentElement.classList.contains("dark");
    if (isDark) {
        document.documentElement.classList.remove("dark");
        localStorage.setItem("theme", "light");
    } else {
        document.documentElement.classList.add("dark");
        localStorage.setItem("theme", "dark");
    }
};
```

---

### 8. **Paginação**
A paginação permite navegar pelos resultados. Botões "Anterior" e "Próximo" permitem avançar ou retroceder nas páginas.

```tsx
<div className="pagination-container">
    <button onClick={() => setCurrentPage(currentPage - 1)} disabled={currentPage === 1}>
        Anterior
    </button>
    {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
        <button
            key={page}
            onClick={() => setCurrentPage(page)}
            className={page === currentPage ? "active" : ""}
        >
            {page}
        </button>
    ))}
    <button onClick={() => setCurrentPage(currentPage + 1)} disabled={currentPage >= totalPages}>
        Próximo
    </button>
</div>
```

---

### Conclusão
Esta aplicação é uma ferramenta robusta para busca e visualização de operadoras de saúde. Com funcionalidades como busca filtrada, paginação, alternância de tema e estilização responsiva, ela oferece uma experiência de usuário moderna e eficiente.


