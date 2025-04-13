import React, { useState, useEffect } from "react";
import axios from "axios";

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

const App: React.FC = () => {

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
    const [theme, setTheme] = useState<"light" | "dark">("light");

    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL || "http://localhost:5000/api";

    useEffect(() => {
        const savedTheme = localStorage.getItem("theme") as "light" | "dark" | null;
        if (savedTheme) {
            setTheme(savedTheme);
            document.documentElement.classList.add(savedTheme);
        } else {
            document.documentElement.classList.add("light");
        }
    }, []);

    const toggleTheme = () => {
        const newTheme = theme === "light" ? "dark" : "light";
        setTheme(newTheme);
        localStorage.setItem("theme", newTheme);
        document.documentElement.classList.remove(theme);
        document.documentElement.classList.add(newTheme);
    };

    const camposDeBusca = [
        { value: "", label: "Todos os campos" },
        { value: "registro_ans", label: "Registro ANS" },
        { value: "cnpj", label: "CNPJ" },
        { value: "razao_social", label: "Razão Social" },
        { value: "nome_fantasia", label: "Nome Fantasia" },
        { value: "modalidade", label: "Modalidade" },
        { value: "logradouro", label: "Logradouro" },
        { value: "numero", label: "Número" },
        { value: "complemento", label: "Complemento" },
        { value: "bairro", label: "Bairro" },
        { value: "cidade", label: "Cidade" },
        { value: "uf", label: "UF" },
        { value: "cep", label: "CEP" },
        { value: "ddd", label: "DDD" },
        { value: "telefone", label: "Telefone" },
        { value: "fax", label: "Fax" },
        { value: "endereco_eletronico", label: "Endereço Eletrônico" },
        { value: "representante", label: "Representante" },
        { value: "cargo_representante", label: "Cargo Representante" },
        { value: "regiao_de_comercializacao", label: "Região de Comercialização" },
        { value: "data_registro_ans", label: "Data Registro ANS" },
    ];

    const searchPlaceholder = filterField
        ? `Pesquisar por ${camposDeBusca.find((c) => c.value === filterField)?.label || "este campo"} (mínimo 2 caracteres)`
        : "Digite o termo de busca (mínimo 2 caracteres)";

    const paginatedResults = results.slice((currentPage - 1) * pageSize, currentPage * pageSize);
    const totalPages = Math.ceil(results.length / pageSize);

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

    const checkApiStatus = async () => {
        try {
            const response = await axios.get(`${apiBaseUrl}/status`);
            setApiStatus(response.data?.status === "online" ? "online" : "offline");
        } catch (error) {
            console.error("Erro ao verificar status da API:", error);
            setApiStatus("offline");
        }
    };

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

    useEffect(() => {
        checkApiStatus();
    }, []);

    return (
        <div className={`min-h-screen ${theme === "dark" ? "bg-gray-900 text-gray-200" : "bg-gray-100 text-gray-800"} flex items-center justify-center p-5`}>
            <div className={`w-full max-w-6xl ${theme === "dark" ? "bg-gray-800" : "bg-white"} shadow-lg rounded-lg p-6`}>

                <header className="flex flex-col md:flex-row items-center justify-between mb-6">
                    <h1 className={`text-2xl md:text-3xl font-bold ${theme === "dark" ? "text-blue-400" : "text-blue-600"} text-center md:text-left`}>
                        Busca de Operadoras de Saúde
                    </h1>
                    <div className="flex flex-col md:flex-row items-center gap-4 mt-4 md:mt-0">
                        <p
                            className={`px-4 py-2 rounded-md text-sm font-medium ${
                                apiStatus === "online"
                                    ? "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300"
                                    : "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300"
                            }`}
                        >
                            API Status: {apiStatus}
                        </p>
                        <button
                            id="theme-toggle"
                            onClick={toggleTheme}
                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition duration-300"
                        >
                            Alternar Tema
                        </button>
                    </div>
                </header>

                <div className="mb-6">
                    <div className="flex flex-col md:flex-row gap-4 mb-4">
                        <select
                            value={filterField}
                            onChange={(e) => setFilterField(e.target.value)}
                            className={`w-full md:w-auto px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                theme === "dark" ? "border-gray-600 bg-gray-700 text-gray-200" : "border-gray-300"
                            }`}
                        >
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
                            type="text"
                            placeholder={searchPlaceholder}
                            className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                theme === "dark" ? "border-gray-600 bg-gray-700 text-gray-200" : "border-gray-300"
                            }`}
                        />
                    </div>
                    <div className="flex flex-col md:flex-row gap-4">
                        <select
                            value={searchMode}
                            onChange={(e) => setSearchMode(e.target.value)}
                            className={`w-full md:w-auto px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                theme === "dark" ? "border-gray-600 bg-gray-700 text-gray-200" : "border-gray-300"
                            }`}
                        >
                            <option value="db">Buscar no Banco de Dados</option>
                            <option value="csv">Buscar no CSV</option>
                        </select>
                        <button
                            onClick={search}
                            className="w-full md:w-auto px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed"
                            disabled={loading}
                        >
                            {loading ? "Buscando..." : "Buscar"}
                        </button>
                    </div>
                </div>

                <div className="mb-6">
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                        {paginatedResults.length} resultado(s) encontrado(s)
                        {lastSearch && ` para "${lastSearch}"`}
                        {filterField && ` em ${camposDeBusca.find((c) => c.value === filterField)?.label}`}
                        (via {searchMode === "db" ? "Banco de Dados" : "CSV"})
                    </p>
                </div>

                {loading && (
                    <div className="flex flex-col items-center justify-center my-8">
                        <div className="w-10 h-10 border-t-4 border-blue-500 border-solid rounded-full animate-spin"></div>
                        <p className="mt-4 text-blue-600 dark:text-blue-400">Carregando resultados...</p>
                    </div>
                )}

                {error && (
                    <div className="p-4 bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-300 rounded-md mb-6">
                        <p>{error}</p>
                    </div>
                )}

                {paginatedResults.length > 0 && (
                    <div className="overflow-x-auto mb-6">
                        <table className="w-full text-sm text-left text-gray-700 dark:text-gray-300">
                            <thead className="bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400 uppercase">
                            <tr>
                                {[
                                    "Relevância",
                                    "Registro ANS",
                                    "CNPJ",
                                    "Razão Social",
                                    "Nome Fantasia",
                                    "Modalidade",
                                    "Logradouro",
                                    "Número",
                                    "Complemento",
                                    "Bairro",
                                    "Cidade",
                                    "UF",
                                    "CEP",
                                    "DDD",
                                    "Telefone",
                                    "Fax",
                                    "Endereço Eletrônico",
                                    "Representante",
                                    "Cargo Representante",
                                    "Região de Comercialização",
                                    "Data Registro ANS",
                                ].map((header) => (
                                    <th key={header} className="px-6 py-3">{header}</th>
                                ))}
                            </tr>
                            </thead>
                            <tbody>
                            {paginatedResults.map((operadora, index) => (
                                <tr
                                    key={index}
                                    className={`border-b ${theme === "dark" ? "bg-gray-900 text-gray-200" : "bg-gray-100 text-gray-800"}  dark:hover:bg-gray-700`}
                                >
                                    <td className="px-6 py-4">{Math.round(operadora.relevance)}%</td>
                                    <td className="px-6 py-4">{operadora.registro_ans}</td>
                                    <td className="px-6 py-4">{operadora.cnpj}</td>
                                    <td className="px-6 py-4">{operadora.razao_social}</td>
                                    <td className="px-6 py-4">{operadora.nome_fantasia}</td>
                                    <td className="px-6 py-4">{operadora.modalidade}</td>
                                    <td className="px-6 py-4">{operadora.logradouro}</td>
                                    <td className="px-6 py-4">{operadora.numero}</td>
                                    <td className="px-6 py-4">{operadora.complemento}</td>
                                    <td className="px-6 py-4">{operadora.bairro}</td>
                                    <td className="px-6 py-4">{operadora.cidade}</td>
                                    <td className="px-6 py-4">{operadora.uf}</td>
                                    <td className="px-6 py-4">{operadora.cep}</td>
                                    <td className="px-6 py-4">{operadora.ddd}</td>
                                    <td className="px-6 py-4">{operadora.telefone}</td>
                                    <td className="px-6 py-4">{operadora.fax}</td>
                                    <td className="px-6 py-4">{operadora.endereco_eletronico}</td>
                                    <td className="px-6 py-4">{operadora.representante}</td>
                                    <td className="px-6 py-4">{operadora.cargo_representante}</td>
                                    <td className="px-6 py-4">{operadora.regiao_de_comercializacao}</td>
                                    <td className="px-6 py-4">{formatDate(operadora.data_registro_ans)}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {(results.length === 0 && (lastSearch || filterField)) ? (
                    <div className="text-center text-gray-600 dark:text-gray-400 mb-6">
                        <p>
                            Nenhum resultado encontrado
                            {lastSearch && ` para "${lastSearch}"`}
                            {filterField && ` em ${camposDeBusca.find((c) => c.value === filterField)?.label}`}
                        </p>
                    </div>
                ) : (!lastSearch && !filterField) ? (
                    <div className="text-center text-gray-600 dark:text-gray-400 mb-6">
                        <p>Digite pelo menos 2 caracteres e clique em Buscar ou selecione um filtro</p>
                        <p className="text-xs mt-2">
                            A pesquisa será feita com base no campo selecionado. Se nenhum campo for selecionado, a busca será feita
                            em todos os campos disponíveis.
                        </p>
                    </div>
                ) : null}


                {paginatedResults.length > 0 && (
                    <div className="flex justify-center gap-2">
                        <button
                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed"
                            onClick={() => setCurrentPage(currentPage - 1)}
                            disabled={currentPage === 1}
                        >
                            Anterior
                        </button>
                        {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                            <button
                                key={page}
                                className={`px-4 py-2 rounded-md ${
                                    page === currentPage
                                        ? "bg-blue-600 text-white"
                                        : "bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600"
                                } transition duration-300`}
                                onClick={() => setCurrentPage(page)}
                            >
                                {page}
                            </button>
                        ))}
                        <button
                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed"
                            onClick={() => setCurrentPage(currentPage + 1)}
                            disabled={currentPage >= totalPages}
                        >
                            Próximo
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default App;