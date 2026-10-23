package br.com.safepay.db;

import br.com.safepay.factory.ConexaoFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ============================================================================
 * INICIALIZADOR AUTOMÁTICO DO BANCO DE DADOS (DDL + SEED)
 * ============================================================================
 * 
 * Papel Pedagógico:
 * - Em ambientes de sala de aula e bancadas de Windows 10, os alunos frequentemente
 *   perdem muito tempo tentando criar tabelas manualmente no terminal do banco.
 * - Esta classe executa a DDL (Data Definition Language) de forma transparente
 *   usando o comando SQL "CREATE TABLE IF NOT EXISTS".
 * - Se o banco acabou de nascer no disco, ela também cadastra usuários de teste
 *   iniciais ("seeds") para podermos realizar as demonstrações de login imediatamente.
 * ============================================================================
 */
public class InicializadorBanco {

    /**
     * Prepara o arquivo do banco de dados na inicialização do sistema.
     */
    public static void inicializar() {
        // Bloco DDL: Criação da Tabela de Usuários
        String sqlCriaTabela = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                login TEXT UNIQUE NOT NULL,
                senha TEXT NOT NULL,
                perfil TEXT NOT NULL
            );
        """;

        /*
         * ❓ O QUE É ESTE TRY COM PARÊNTESES? (Try-With-Resources):
         * 
         * No Java moderno (Java 7+), qualquer recurso que implemente a interface
         * 'AutoCloseable' colocado dentro dos parênteses do try(...) será FECHADO
         * AUTOMATICAMENTE ao final do bloco.
         * 
         * Isso elimina o risco mortal de "Connection Leak" (deixar a torneira do banco
         * aberta na memória do computador até o sistema travar).
         */
        try (Connection conn = ConexaoFactory.obterConexao();
             Statement stmt = conn.createStatement()) {

            // Executa o comando DDL no SQLite
            stmt.execute(sqlCriaTabela);

            // Verifica se a tabela já possui dados ou se precisa ser semeada
            if (tabelaEstaVazia(conn)) {
                popularDadosIniciais(conn);
            }

        } catch (SQLException e) {
            System.err.println("❌ Falha crítica ao inicializar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Consulta auxiliar para checar se a tabela de usuários possui algum registro.
     */
    private static boolean tabelaEstaVazia(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM usuarios";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            /*
             * ❓ POR QUE PRECISAMOS DE "rs.next()"?
             * 
             * Quando o banco devolve a resposta de um SELECT, o cursor do ResultSet
             * fica posicionado no limbo (ANTES da primeira linha).
             * O método rs.next() anda uma linha para frente:
             * - Se existir linha, ele retorna 'true' e posiciona na linha.
             * - Se a tabela estiver vazia, retorna 'false'.
             * 
             * Tentar ler colunas sem chamar rs.next() gera um erro fatal de cursor inválido!
             */
            if (rs.next()) {
                int total = rs.getInt("total");
                return total == 0;
            }
        }
        return true;
    }

    /**
     * Insere usuários fictícios de teste para as demonstrações de ataque e defesa.
     */
    private static void popularDadosIniciais(Connection conn) throws SQLException {
        System.out.println("🌱 Banco de dados novo detectado! Populando usuários iniciais de teste...");

        /*
         * ❓ O QUE SÃO ESSES PONTOS DE INTERROGAÇÃO (?) ?
         * 
         * Eles são 'Placeholders' (marcadores de posição).
         * Em vez de colar variáveis com aspas na string SQL, deixamos os '?' no lugar.
         * O PreparedStatement cuidará de encaixar cada dado no tipo exato,
         * sem risco de corromper a instrução SQL.
         */
        String sqlInsert = "INSERT INTO usuarios (nome, login, senha, perfil) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

            // Usuário 1: Administrador do Sistema (Alvo preferido dos hackers)
            // ⚠️ ATENÇÃO: No JDBC, os parâmetros começam no índice 1, NUNCA NO ZERO!
            stmt.setString(1, "Carlos Drummond (Diretor de TI)"); // 1º ? -> nome
            stmt.setString(2, "admin");                          // 2º ? -> login
            stmt.setString(3, "SegredoUltraForte#2026");          // 3º ? -> senha
            stmt.setString(4, "DIRETOR");                         // 4º ? -> perfil
            stmt.executeUpdate(); // executeUpdate() é usado para INSERT, UPDATE ou DELETE!

            // Usuário 2: Funcionário Comum
            stmt.setString(1, "Mariana Lima (Analista Financeiro)");
            stmt.setString(2, "mariana");
            stmt.setString(3, "financas123");
            stmt.setString(4, "ANALISTA");
            stmt.executeUpdate();

            // Usuário 3: Estagiário
            stmt.setString(1, "Pedro Silva (Estagiario de Suporte)");
            stmt.setString(2, "pedro");
            stmt.setString(3, "cafezinho99");
            stmt.setString(4, "ESTAGIARIO");
            stmt.executeUpdate();

            System.out.println("✅ Três usuários de teste foram inseridos com sucesso na tabela 'usuarios'.");
        }
    }
}
