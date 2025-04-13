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


