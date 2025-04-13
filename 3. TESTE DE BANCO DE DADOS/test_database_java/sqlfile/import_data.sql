-- Habilita carregamento local (caso necessário)
SET GLOBAL local_infile = 1;

-- Importa os dados da tabela operadoras
LOAD DATA LOCAL INFILE '/downloads/operadoras/Relatorio_cadop.csv'
INTO TABLE operadoras
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(registro_ans, cnpj, razao_social, nome_fantasia, modalidade, uf, municipio, data_registro, situacao);


-- Importa os dados da tabela demonstracoes_contabeis
LOAD DATA LOCAL INFILE '/downloads/demonstracoes/2024/1T2024.csv'
INTO TABLE demonstracoes_contabeis
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(registro_ans, razao_social, tipo_despesa, ano, trimestre, valor_despesa, tipo_registro, competencia);
