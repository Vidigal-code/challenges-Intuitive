<style>

:root {
  --bg-color: #f8f9fa;
  --text-color: #212529;
  --card-color: #ffffff;
  --header-color: #0d6efd;
  --border-color: #dee2e6;
  --shadow-color: rgba(0, 0, 0, 0.1);
  --accent-color: #0d6efd;
  --accent-hover: #0b5ed7;
  --error-bg: #f8d7da;
  --error-color: #dc3545;
  --table-header-bg: #e9ecef;
  --table-row-hover: #f2f2f2;
  --table-border: #dee2e6;
  --input-bg: #fff;
  --input-border: #ced4da;
  --input-focus: #0d6efd;
  --text-label-color: #495057;
}

[data-theme="dark"] {
  --bg-color: #121212;
  --text-color: #e9ecef;
  --card-color: #1e1e1e;
  --header-color: #4dabf7;
  --border-color: #343a40;
  --shadow-color: rgba(0, 0, 0, 0.5);
  --accent-color: #4dabf7;
  --accent-hover: #3c90d9;
  --error-bg: #38181c;
  --error-color: #f8d7da;
  --table-header-bg: #343a40;
  --table-row-hover: #2c2c2c;
  --table-border: #454d55;
  --input-bg: #2c2c2c;
  --input-border: #495057;
  --input-focus: #4dabf7;
  --text-label-color: #adb5bd;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html, body {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  background-color: var(--bg-color);
  color: var(--text-color);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  transition: background-color 0.3s, color 0.3s;
  padding: 5px;
  overflow-y: auto; 
}

.app-container {
  max-width: 100%;
  width: 100%;
  padding: 15px 10px;
  background-color: var(--card-color);
  border-radius: 12px;
  box-shadow: 0 5px 25px var(--shadow-color);
  transition: all 0.3s ease;
  margin: 5px auto;
}

@media (min-width: 480px) {
  .app-container {
    padding: 20px 15px;
    margin: 10px auto;
  }
}

@media (min-width: 768px) {
  .app-container {
    width: 95%;
    max-width: 1200px;
    padding: 25px 20px;
    margin: 15px auto;
  }
}

.app-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 15px;
  gap: 12px;
  width: 100%;
}

.app-header h1 {
  margin: 0;
  color: var(--header-color);
  font-size: clamp(1.1rem, 3.5vw, 2.2rem);
  font-weight: 700;
  text-align: center;
  width: 100%;
}

.header-actions {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 8px;
  margin-top: 8px;
}

@media (min-width: 480px) {
  .header-actions {
    flex-direction: row;
    justify-content: space-between;
  }
}

.status-indicator {
  font-weight: 600;
  padding: 8px;
  border-radius: 8px;
  text-transform: capitalize;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  margin: 0;
  transition: all 0.3s ease;
}

@media (min-width: 480px) {
  .status-indicator {
    flex: 1;
    max-width: 48%;
    font-size: 0.85rem;
    padding: 10px;
  }
}

.status-indicator::before {
  content: "";
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: background-color 0.3s ease;
}

@media (min-width: 480px) {
  .status-indicator::before {
    width: 10px;
    height: 10px;
  }
}

.online {
  background-color: rgba(40, 167, 69, 0.15);
  color: #28a745;
}

.online::before {
  background-color: #28a745;
}

.offline {
  background-color: rgba(220, 53, 69, 0.15);
  color: #dc3545;
}

.offline::before {
  background-color: #dc3545;
}

#theme-toggle {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

@media (min-width: 480px) {
  #theme-toggle {
    flex: 1;
    max-width: 48%;
    gap: 8px;
  }
}

.search-container {
  margin-bottom: 15px;
  width: 100%;
}

@media (min-width: 768px) {
  .search-container {
    margin-bottom: 20px;
  }
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

@media (min-width: 768px) {
  .search-form {
    gap: 10px;
  }
}

.filter-row,
.action-row {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 8px;
}

@media (min-width: 480px) {
  .filter-row,
  .action-row {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
  }
}

@media (min-width: 768px) {
  .filter-row,
  .action-row {
    gap: 10px;
  }
}

.filter-field,
.search-input,
.search-mode {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--input-border);
  border-radius: 8px;
  font-size: 14px;
  background-color: var(--input-bg);
  color: var(--text-color);
  transition: all 0.3s ease;
  box-sizing: border-box;
  -webkit-appearance: none;
  appearance: none;
}

@media (min-width: 480px) {
  .filter-field,
  .search-input,
  .search-mode {
    font-size: 15px;
    padding: 11px 14px;
  }
}

@media (min-width: 768px) {
  .filter-field,
  .search-input {
    flex: 1;
    min-width: 120px;
    font-size: 16px;
    padding: 12px 16px;
  }

  .search-mode {
    flex: 1;
    min-width: 200px;
    font-size: 16px;
    padding: 12px 16px;
  }
}

.filter-field:focus,
.search-input:focus,
.search-mode:focus {
  border-color: var(--input-focus);
  outline: none;
  box-shadow: 0 0 0 3px rgba(77, 171, 247, 0.25);
}

.search-button {
  width: 100%;
  padding: 10px 16px;
  background-color: var(--accent-color);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

@media (min-width: 480px) {
  .search-button {
    flex: 0 0 auto;
    min-width: 100px;
    font-size: 15px;
    padding: 11px 18px;
  }
}

@media (min-width: 768px) {
  .search-button {
    min-width: 120px;
    font-size: 16px;
    padding: 12px 20px;
    gap: 8px;
  }
}

.search-button:hover {
  background-color: var(--accent-hover);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.search-button:active {
  transform: translateY(0);
}

.search-button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.search-info {
  margin-top: 12px;
  font-size: 0.8rem;
  word-wrap: break-word;
  color: var(--text-color);
  opacity: 0.8;
}

@media (min-width: 480px) {
  .search-info {
    font-size: 0.85rem;
  }
}

@media (min-width: 768px) {
  .search-info {
    margin-top: 15px;
    font-size: 0.9rem;
  }
}

.loading {
  text-align: center;
  margin: 25px 0;
}

.spinner {
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-top: 3px solid var(--accent-color);
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@media (min-width: 768px) {
  .loading {
    margin: 30px 0;
  }
  
  .spinner {
    border-width: 4px;
    width: 40px;
    height: 40px;
    margin: 0 auto 20px;
  }
}

[data-theme="dark"] .spinner {
  border-color: rgba(255, 255, 255, 0.1);
  border-top-color: var(--accent-color);
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error-message {
  padding: 12px;
  background-color: var(--error-bg);
  color: var(--error-color);
  border: 1px solid var(--error-color);
  border-radius: 8px;
  margin-bottom: 15px;
  font-weight: 500;
  font-size: 0.9rem;
}

@media (min-width: 768px) {
  .error-message {
    padding: 15px;
    margin-bottom: 20px;
    font-size: 1rem;
  }
}

.results-container {
  width: 100%;
  overflow-x: auto;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  margin-bottom: 15px;
  -webkit-overflow-scrolling: touch; 
  scrollbar-width: thin; 
  scrollbar-color: var(--accent-color) var(--bg-color);
}

@media (min-width: 768px) {
  .results-container {
    margin-bottom: 20px;
  }
}

.results-container::-webkit-scrollbar {
  height: 6px;
}

@media (min-width: 768px) {
  .results-container::-webkit-scrollbar {
    height: 8px;
  }
}

.results-container::-webkit-scrollbar-track {
  background: var(--bg-color);
  border-radius: 4px;
}

.results-container::-webkit-scrollbar-thumb {
  background-color: var(--accent-color);
  border-radius: 4px;
}

.results-table {
  width: 100%;
  border-collapse: collapse;
}

@media (max-width: 767px) {
  .results-table, 
  .results-table tbody, 
  .results-table tr {
    display: block;
    width: 100%;
  }
  
  .results-table thead {
    display: none;
  }
  
  .results-table tbody tr {
    margin-bottom: 12px;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    padding: 8px;
    background-color: var(--card-color);
    box-shadow: 0 1px 3px var(--shadow-color);
  }
  
  .results-table td {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 8px;
    text-align: right;
    border-bottom: 1px solid var(--border-color);
    font-size: 0.85rem;
    min-height: 36px;
  }
  
  .results-table td:last-child {
    border-bottom: none;
  }
  
  .results-table td::before {
    content: attr(data-label);
    font-weight: 600;
    text-align: left;
    flex: 1;
    margin-right: 8px;
    color: var(--text-label-color);
  }
}

@media (max-width: 359px) {
  .results-table td {
    flex-direction: column;
    align-items: flex-start;
    text-align: left;
    padding: 4px 6px;
    font-size: 0.8rem;
  }
  
  .results-table td::before {
    margin-bottom: 4px;
    margin-right: 0;
    width: 100%;
  }
}

@media (min-width: 768px) {
  .results-table {
    width: 100%;
    table-layout: auto;
    min-width: 650px;
  }
  
  .results-table th,
  .results-table td {
    padding: 12px 8px;
    text-align: center;
    border-bottom: 1px solid var(--table-border);
    font-size: 0.9rem;
    white-space: nowrap;
    transition: background-color 0.2s;
  }
  
  @media (min-width: 992px) {
    .results-table th,
    .results-table td {
      padding: 15px 10px;
      font-size: 0.95rem;
    }
  }
  
  .results-table th {
    background-color: var(--table-header-bg);
    font-weight: 600;
    text-transform: uppercase;
    font-size: 0.8rem;
    letter-spacing: 0.5px;
    position: sticky;
    top: 0;
    z-index: 10;
  }
  
  @media (min-width: 992px) {
    .results-table th {
      font-size: 0.85rem;
    }
  }
  
  .results-table tr:last-child td {
    border-bottom: none;
  }
  
  .results-table tr:hover td {
    background-color: var(--table-row-hover);
  }
  
  .text-left {
    text-align: left;
  }
}

.pagination-container {
  width: 100%;
  overflow-x: auto;
  padding-bottom: 5px;
  -webkit-overflow-scrolling: touch;
  margin-top: 15px;
}

@media (min-width: 768px) {
  .pagination-container {
    margin-top: 20px;
  }
}

.pagination-buttons {
  display: flex;
  justify-content: center;
  gap: 6px;
  flex-wrap: nowrap;
  min-width: max-content;
  padding: 3px 0;
}

@media (min-width: 768px) {
  .pagination-buttons {
    gap: 8px;
    padding: 5px 0;
  }
}

.pagination-button {
  padding: 6px 10px;
  background-color: var(--accent-color);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s ease;
  min-width: 32px;
  font-weight: 500;
}

@media (min-width: 480px) {
  .pagination-button {
    padding: 8px 12px;
    font-size: 14px;
    min-width: 35px;
  }
}

@media (min-width: 768px) {
  .pagination-button {
    padding: 10px 15px;
    font-size: 15px;
    min-width: 40px;
  }
}

.pagination-button:hover {
  background-color: var(--accent-hover);
  transform: translateY(-2px);
}

.pagination-button.active {
  background-color: var(--accent-hover);
  cursor: default;
  transform: none;
  box-shadow: 0 0 0 3px rgba(77, 171, 247, 0.25);
}

.pagination-button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  transform: none;
  opacity: 0.6;
}

.no-results,
.instructions {
  text-align: center;
  font-size: 0.9rem;
  margin: 20px 0;
  color: var(--text-color);
}

@media (min-width: 480px) {
  .no-results,
  .instructions {
    font-size: 1rem;
    margin: 25px 0;
  }
}

@media (min-width: 768px) {
  .no-results,
  .instructions {
    font-size: 1.1rem;
    margin: 40px 0;
  }
}

.info-text {
  font-size: 0.8rem;
  margin-top: 10px;
  opacity: 0.8;
  line-height: 1.4;
}

@media (min-width: 480px) {
  .info-text {
    font-size: 0.85rem;
    line-height: 1.5;
  }
}

@media (min-width: 768px) {
  .info-text {
    font-size: 0.9rem;
    margin-top: 15px;
  }
}

@media (max-width: 320px) {
  .app-container {
    padding: 10px 8px;
  }

  .app-header h1 {
    font-size: clamp(1rem, 3vw, 1.2rem);
  }

  .status-indicator,
  #theme-toggle {
    font-size: 0.75rem;
    padding: 6px;
  }

  .filter-field,
  .search-input,
  .search-mode,
  .search-button {
    padding: 8px 10px;
    font-size: 13px;
  }
}

  
</style>

<template>
  <div id="app">
     <div class="app-container">
       <header class="app-header">
         <h1>Busca de Operadoras de Saúde</h1>
         <div class="header-actions">
           <p
             class="status-indicator"
             :class="{
               online: apiStatus === 'online',
               offline: apiStatus === 'offline',
             }"
           >
             API Status: {{ apiStatus }}
           </p>
           <button id="theme-toggle" class="search-button">Alternar Tema</button>
         </div>
       </header>
       <div class="search-container">
         <div class="search-form">
           <div class="filter-row">
             <select v-model="filterField" class="filter-field">
               <option
                 v-for="campo in uniqueCamposDeBusca"
                 :key="campo.value"
                 :value="campo.value"
               >
                 {{ campo.label }}
               </option>
             </select>
             <input
               v-model="searchTerm"
               @keyup.enter="search"
               type="text"
               :placeholder="searchPlaceholder"
               class="search-input"
             />
           </div>
           <div class="action-row">
             <select v-model="searchMode" class="search-mode">
               <option value="db">Buscar no Banco de Dados</option>
               <option value="csv">Buscar no CSV</option>
             </select>
             <button @click="search" class="search-button" :disabled="loading">
               {{ loading ? "Buscando..." : "Buscar" }}
             </button>
           </div>
         </div>
         <div class="search-info" v-if="lastSearch || filterField">
           <p>
             {{ paginatedResults.length }} resultado(s) encontrado(s)
             <span v-if="lastSearch">para "{{ lastSearch }}"</span>
             <span v-if="filterField"> em {{ getFilterLabel() }}</span>
             (via {{ searchMode === "db" ? "Banco de Dados" : "CSV" }})
           </p>
         </div>
       </div>
       <div v-if="loading" class="loading">
         <div class="spinner"></div>
         <p>Carregando resultados...</p>
       </div>
       <div v-else-if="error" class="error-message">
         <p>{{ error }}</p>
       </div>
       <div v-else-if="paginatedResults.length > 0" class="results-container">
         <table class="results-table">
           <thead>
             <tr>
               <th>Relevância</th>
               <th>Registro ANS</th>
               <th>CNPJ</th>
               <th>Razão Social</th>
               <th>Nome Fantasia</th>
               <th>Modalidade</th>
               <th>Logradouro</th>
               <th>Número</th>
               <th>Complemento</th>
               <th>Bairro</th>
               <th>Cidade</th>
               <th>UF</th>
               <th>CEP</th>
               <th>DDD</th>
               <th>Telefone</th>
               <th>Fax</th>
               <th>Endereço Eletrônico</th>
               <th>Representante</th>
               <th>Cargo Representante</th>
               <th>Região de Comercialização</th>
               <th>Data Registro ANS</th>
             </tr>
           </thead>
           <tbody>
             <tr v-for="(operadora, index) in paginatedResults" :key="index">
               <td data-label="Relevância">{{ Math.round(operadora.relevance) }}%</td>
               <td data-label="Registro ANS">{{ operadora.registro_ans }}</td>
               <td data-label="CNPJ">{{ operadora.cnpj }}</td>
               <td data-label="Razão Social" class="text-left">{{ operadora.razao_social }}</td>
               <td data-label="Nome Fantasia" class="text-left">{{ operadora.nome_fantasia }}</td>
               <td data-label="Modalidade">{{ operadora.modalidade }}</td>
               <td data-label="Logradouro">{{ operadora.logradouro }}</td>
               <td data-label="Número">{{ operadora.numero }}</td>
               <td data-label="Complemento">{{ operadora.complemento }}</td>
               <td data-label="Bairro">{{ operadora.bairro }}</td>
               <td data-label="Cidade">{{ operadora.cidade }}</td>
               <td data-label="UF">{{ operadora.uf }}</td>
               <td data-label="CEP">{{ operadora.cep }}</td>
               <td data-label="DDD">{{ operadora.ddd }}</td>
               <td data-label="Telefone">{{ operadora.telefone }}</td>
               <td data-label="Fax">{{ operadora.fax }}</td>
               <td data-label="Endereço Eletrônico">{{ operadora.endereco_eletronico }}</td>
               <td data-label="Representante">{{ operadora.representante }}</td>
               <td data-label="Cargo Representante">{{ operadora.cargo_representante }}</td>
               <td data-label="Região de Comercialização">{{ operadora.regiao_de_comercializacao }}</td>
               <td data-label="Data Registro ANS">{{ formatDate(operadora.data_registro_ans) }}</td>
             </tr>
           </tbody>
         </table>
       </div>
       <div v-else-if="lastSearch || filterField" class="no-results">
         <p>
           Nenhum resultado encontrado
           <span v-if="lastSearch"> para "{{ lastSearch }}"</span>
           <span v-if="filterField"> em {{ getFilterLabel() }}</span>
         </p>
       </div>
       <div v-else class="instructions">
         <p>
           Digite pelo menos 2 caracteres e clique em Buscar ou selecione um filtro
         </p>
         <p class="info-text">
           A pesquisa será feita com base no campo selecionado. Se nenhum campo for
           selecionado, a busca será feita em todos os campos disponíveis.
         </p>
       </div>
       <div class="pagination-container" v-if="paginatedResults.length > 0">
         <div class="pagination-buttons">
           <button
             class="pagination-button"
             @click="prevPage"
             :disabled="currentPage === 1"
           >
             Anterior
           </button>
           <button
             v-for="page in totalPagesArray"
             :key="page"
             class="pagination-button"
             :class="{ active: page === currentPage }"
             @click="goToPage(page)"
           >
             {{ page }}
           </button>
           <button
             class="pagination-button"
             @click="nextPage"
             :disabled="currentPage >= totalPages"
           >
             Próximo
           </button>
         </div>
       </div>
     </div>
   </div>
 </template>
 
 <script>
 import axios from "axios";
 
 export default {
   name: "App",
   data() {
     return {
       searchTerm: "",
       searchMode: "db",
       filterField: "",
       lastSearch: "",
       results: [],
       loading: false,
       error: null,
       apiStatus: "verificando...",
       apiBaseUrl: process.env.VUE_APP_API_BASE_URL,
       camposDeBusca: [
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
         { value: "data_registro_ans", label: "Data Registro ANS" }
       ],
       currentPage: 1,
       pageSize: 10,
     };
   },
   computed: {
     uniqueCamposDeBusca() {
       const seen = new Set();
       return this.camposDeBusca.filter((campo) => {
         if (seen.has(campo.value)) return false;
         seen.add(campo.value);
         return true;
       });
     },
     searchPlaceholder() {
       if (this.filterField) {
         const field = this.camposDeBusca.find(
           (c) => c.value === this.filterField
         );
         return `Pesquisar por ${
           field ? field.label : "este campo"
         } (mínimo 2 caracteres)`;
       }
       return "Digite o termo de busca (mínimo 2 caracteres)";
     },
     paginatedResults() {
       const start = (this.currentPage - 1) * this.pageSize;
       const end = start + this.pageSize;
       return this.results.slice(start, end);
     },
     totalPages() {
       return Math.ceil(this.results.length / this.pageSize);
     },
     totalPagesArray() {
       return Array.from({ length: this.totalPages }, (_, i) => i + 1);
     },
   },
   mounted() {
     this.checkApiStatus();
     this.fetchCampos();
     const toggleButton = document.getElementById("theme-toggle");
     const currentTheme = localStorage.getItem("theme");
     if (currentTheme === "dark") {
       document.documentElement.setAttribute("data-theme", "dark");
     }
     toggleButton?.addEventListener("click", () => {
       const isDark =
         document.documentElement.getAttribute("data-theme") === "dark";
       if (isDark) {
         document.documentElement.setAttribute("data-theme", "light");
         localStorage.setItem("theme", "light");
       } else {
         document.documentElement.setAttribute("data-theme", "dark");
         localStorage.setItem("theme", "dark");
       }
     });
   },
   methods: {
     formatDate(dateString) {
       if (!dateString) return '';
       
       try {
         const date = new Date(dateString);
         
         if (isNaN(date.getTime())) {
           return dateString;
         }
         
         const day = date.getDate().toString().padStart(2, '0');
         const month = (date.getMonth() + 1).toString().padStart(2, '0');
         const year = date.getFullYear();
         
         return `${day}/${month}/${year}`;
       } catch (e) {
         console.error("Error formatting date:", e);
         return dateString;
       }
     },
     async search() {
       if ((!this.searchTerm || this.searchTerm.length < 2) && !this.filterField) {
         this.error = "Por favor, digite pelo menos 2 caracteres para buscar ou selecione um filtro";
         return;
       }
       this.loading = true;
       this.error = null;
       this.lastSearch = this.searchTerm;
       this.currentPage = 1;
       this.results = [];
       try {
         const endpoint =
           this.searchMode === "db"
             ? `${this.apiBaseUrl}/operadoras/db`
             : `${this.apiBaseUrl}/operadoras/csv`;
         const response = await axios.get(endpoint, {
           params: {
             q: this.searchTerm,
             filter: this.filterField
           },
         });
         if (response.data && Array.isArray(response.data.results)) {
           this.results = response.data.results;
         } else {
           this.results = [];
           this.error = "Formato de resposta inválido da API";
         }
       } catch (error) {
         console.error("Erro ao buscar:", error);
         this.error =
           error.response?.data?.error ||
           "Erro ao buscar operadoras. Verifique se o servidor está online.";
         this.results = [];
       } finally {
         this.loading = false;
       }
     },
     prevPage() {
       if (this.currentPage > 1) {
         this.currentPage--;
       }
     },
     nextPage() {
       if (this.currentPage < this.totalPages) {
         this.currentPage++;
       }
     },
     goToPage(page) {
       this.currentPage = page;
     },
     async checkApiStatus() {
       try {
         const response = await axios.get(`${this.apiBaseUrl}/status`);
         this.apiStatus =
           response.data?.status === "online" ? "online" : "offline";
       } catch (error) {
         console.error("Erro ao verificar status da API:", error);
         this.apiStatus = "offline";
       }
     },
     async fetchCampos() {
       try {
         const response = await axios.get(`${this.apiBaseUrl}/campos`);
         if (response.data?.campos && Array.isArray(response.data.campos)) {
           this.camposDeBusca = response.data.campos;
         }
       } catch (error) {
         console.warn("Erro ao carregar campos de busca. Usando padrão.");
       }
     },
     getFilterLabel() {
       const field = this.camposDeBusca.find(
         (c) => c.value === this.filterField
       );
       return field ? field.label : "Todos os campos";
     },
   },
 };
 </script>