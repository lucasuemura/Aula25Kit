# SafePay FinTech • Kit de Desenvolvimento de Laboratório (Aula 25)

> **Engenharia de Persistência: JDBC, Arquitetura e Defesa contra SQL Injection**  
> Disciplina de Programação Orientada a Objetos • UniCesumar  
> Prof. Mestre Júlio B. Herculani

Este kit de laboratório foi preparado para que você experimente, na prática da bancada de laboratório, a conexão entre o Java e o banco de dados relacional, testemunhando a gravidade do ataque **SQL Injection** e implementando a blindagem industrial definitiva com **PreparedStatement** e **Try-With-Resources**.

---

## 🎯 Objetivos de Aprendizagem
1. Compreender o papel da `ConexaoFactory` para centralização e desacoplamento de credenciais.
2. Explorar o banco de dados embutido **SQLite** (sem precisar de servidores ou instalações pesadas).
3. Entender na prática a falha de concatenação com `Statement` e o clássico golpe `' OR '1'='1' --`.
4. Dominar a substituição de parâmetros dinâmicos com interrogações `?` no `PreparedStatement`.
5. Fixar a convenção internacional de índices JDBC que **começam no 1** (1-based index).

---

## 📂 Arquitetura do Pacote
```text
Aula25Kit
├── .vscode/
│   └── settings.json                    <- Configuração automática do VS Code
├── .classpath & .project                <- Configuração nativa de IDEs Eclipse/VS Code
├── lib/
│   └── sqlite-jdbc-3.45.1.0.jar         <- Driver JDBC do SQLite embutido
├── src/br/com/safepay/
│   ├── model/
│   │   └── Usuario.java                 <- POJO com os campos do usuário
│   ├── factory/
│   │   └── ConexaoFactory.java          <- Fábrica de conexões (Padrão GoF Factory)
│   ├── db/
│   │   └── InicializadorBanco.java      <- DDL automático (CREATE TABLE e carga de dados)
│   ├── seguranca/
│   │   ├── DemonstracaoVulneravelStatement.java   <- Código que sofre o ataque
│   │   └── DemonstracaoSeguraPreparedStatement.java <- Código blindado com PreparedStatement
│   └── main/
│       └── MainApp.java                 <- Menu interativo de console para testes
├── bin/                                 <- Bytecodes compilados (.class)
├── executar.bat                         <- Script de 1 clique para Windows 10
└── safepay.db                           <- Criado automaticamente na primeira execução
```

---

## 🚀 Como Executar no Windows 10

### Opção A: Execução Rápida via Arquivo de Lote (Recomendado no Laboratório)
1. Dê um **duplo clique** no arquivo `executar.bat`.
2. O script compilará todas as classes e iniciará o menu interativo automaticamente no terminal do Windows.

### Opção B: No VS Code
1. Abra a pasta `Aula25Kit` no VS Code (`Arquivo -> Abrir Pasta...`).
2. Abra o arquivo `src/br/com/safepay/main/MainApp.java`.
3. Clique no botão **Run** (ou pressione `F5`).
4. Utilize o terminal integrado do VS Code para navegar pelo menu interativo!

### Opção C: No Eclipse ou IntelliJ
1. Selecione `Import Existing Java Project` apontando para a pasta `Aula25Kit`.
2. A pasta `lib/sqlite-jdbc-3.45.1.0.jar` já está declarada no `.classpath`.
3. Clique com o botão direito em `MainApp.java` -> `Run As` -> `Java Application`.

---

## 🧪 Roteiro da Missão de Bancada

1. **Passo 1 (Carga e Inspeção):**
   - Execute a aplicação e selecione a **Opção [4]** para ver os usuários cadastrados automaticamente no arquivo `safepay.db`.
   - Repare que o usuário com ID 1 é o `Carlos Drummond (Diretor de TI)`.

2. **Passo 2 (O Teste de Penetração):**
   - Selecione a **Opção [2]** para simular o ataque de SQL Injection no modo vulnerável.
   - Observe no console a query gerada pela concatenação:
     `SELECT * FROM usuarios WHERE login = '' OR '1'='1' --' AND senha = '...'`
   - Perceba que o invasor ganhou acesso total de Administrador sem saber a senha!

3. **Passo 3 (A Barreira do PreparedStatement):**
   - Selecione a **Opção [3]** para submeter o mesmíssimo ataque contra o `PreparedStatement`.
   - Veja como o banco trata o texto como um dado literal inofensivo e rejeita o login com segurança.

4. **Passo 4 (Leitura do Código Comentado):**
   - Abra a classe `ConexaoFactory.java` e leia a explicação sobre o porquê do construtor privado e a vantagem de centralizar a URL.
   - Abra a classe `DemonstracaoSeguraPreparedStatement.java` e estude os métodos `stmt.setString(1, ...)` e `stmt.setString(2, ...)`.
