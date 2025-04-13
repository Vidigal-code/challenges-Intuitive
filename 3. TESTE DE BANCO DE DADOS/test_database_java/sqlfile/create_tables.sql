-- Tabela de operadoras ativas na ANS
CREATE TABLE IF NOT EXISTS operadoras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    registro_ans VARCHAR(20),
    cnpj VARCHAR(18),
    razao_social VARCHAR(255),
    nome_fantasia VARCHAR(255),
    modalidade VARCHAR(100),
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    uf CHAR(2),
    cep VARCHAR(9),
    ddd VARCHAR(3),
    telefone VARCHAR(30), 
    fax VARCHAR(30),      
    endereco_eletronico VARCHAR(255),
    representante VARCHAR(255), 
    cargo_representante VARCHAR(255), 
    regiao_de_comercializacao VARCHAR(100), 
    data_registro_ans DATE
);

-- Tabela de demonstrações contábeis (baseada nos dados da ANS)
CREATE TABLE IF NOT EXISTS demonstracoes_contabeis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data DATE,
    reg_ans VARCHAR(20),
    cd_conta_contabil VARCHAR(20),
    descricao VARCHAR(255),
    vl_saldo_inicial DECIMAL(18,2),
    vl_saldo_final DECIMAL(18,2)
);
