package br.com.safepay.seguranca;

import br.com.safepay.factory.ConexaoFactory;
import br.com.safepay.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ============================================================================
 * DEMONSTRAÇÃO DE CÓDIGO SEGURO E INDUSTRIAL (PADRÃO RECOMENDADO)
 * ============================================================================
 * 
 * 🛡️ A MURALHA DE DEFESA: PREPAREDSTATEMENT & PLACEHOLDERS (?)
 * 
 * ❓ O QUE É O PREPAREDSTATEMENT?
 * É uma instrução SQL pré-compilada. Ao contrário do Statement que envia tudo
 * misturado em texto plano, o PreparedStatement opera em duas fases:
 * 
 * FASE 1: O esqueleto SQL é enviado ao banco com pontos de interrogação (?).
 *         O banco de dados compila o comando e cria a árvore de decisão (Query Plan).
 *         O banco agora "sabe" exatamente a gramática da consulta.
 * 
 * FASE 2: O Java injeta os valores reais através dos métodos tipados:
 *         stmt.setString(1, login);
 *         stmt.setString(2, senha);
 *         O banco sabe com 100% de certeza que o conteúdo dessas variáveis é
 *         APENAS DADO PURO e NUNCA poderá ser interpretado como código executável!
 * ============================================================================
 */
public class DemonstracaoSeguraPreparedStatement {

    /**
     * Autentica o usuário de forma 100% segura contra SQL Injection.
     * 
     * @param login Digitado pelo usuário.
     * @param senha Digitada pelo usuário.
     * @return O objeto Usuario se as credenciais conferirem, ou null se falhar.
     */
    public static Usuario autenticarComSeguranca(String login, String senha) {
        /*
         * ❓ O QUE É ESSE PONTO DE INTERROGAÇÃO (?) ?
         * 
         * O ponto de interrogação '?' é um parâmetro posicional ("placeholder").
         * Ele diz ao banco: "Reserve este espaço para receber um valor literal depois".
         * 
         * Note que NÃO colocamos aspas simples em volta do '?'!
         * O driver JDBC se encarrega de formatar, escapar e tipar o dado automaticamente.
         */
        String sql = "SELECT id, nome, login, perfil FROM usuarios WHERE login = ? AND senha = ?";

        System.out.println("\n--------------------------------------------------");
        System.out.println("🛡️ [MODO SEGURO - PREPAREDSTATEMENT PARAMETRIZADO]");
        System.out.println("📜 Esqueleto SQL compilado pelo banco de dados:");
        System.out.println("👉 " + sql);
        System.out.println("📌 Parâmetro 1 (?) preenchido com: \"" + login + "\"");
        System.out.println("📌 Parâmetro 2 (?) preenchido com: \"" + senha + "\"");
        System.out.println("--------------------------------------------------");

        /*
         * Try-with-resources: Garante o fechamento automático da Connection,
         * do PreparedStatement e do ResultSet, prevenindo vazamentos de memória (leaks).
         */
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            /*
             * ⚠️ A PEGADINHA DO ÍNDICE 1-BASED DO JDBC:
             * 
             * No Java comum, listas e arrays começam no índice 0 (vetor[0]).
             * No JDBC, os parâmetros seguem o padrão do ANSI SQL:
             * - O PRIMEIRO '?' é o índice 1!
             * - O SEGUNDO '?' é o índice 2!
             * 
             * Se você colocar stmt.setString(0, ...), o Java lança SQLException na hora!
             */
            stmt.setString(1, login); // Substitui com segurança o 1º '?' (login)
            stmt.setString(2, senha); // Substitui com segurança o 2º '?' (senha)

            /*
             * ❓ POR QUE "stmt.executeQuery()" NÃO RECEBE A STRING SQL COMO PARÂMETRO?
             * 
             * Repare bem:
             * No Statement comum: stmt.executeQuery(sql); (passava a string)
             * No PreparedStatement: stmt.executeQuery();  (parênteses vazios!)
             * 
             * Por que isso? Porque a string 'sql' já foi entregue ao banco lá em cima,
             * na chamada "conn.prepareStatement(sql)"! O banco já compilou a query;
             * agora você só aperta o gatilho para disparar.
             */
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNome(rs.getString("nome"));
                    u.setLogin(rs.getString("login"));
                    u.setPerfil(rs.getString("perfil"));
                    return u;
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Falha de comunicação com o banco: " + e.getMessage());
            return null;
        }
    }

    /**
     * Explica por que o ataque falha miseravelmente contra o PreparedStatement.
     */
    public static void explicarDefesa() {
        System.out.println("""
        
        ╔══════════════════════════════════════════════════════════════════════════════════╗
        ║          POR QUE O ATAQUE ' OR '1'='1' FALHOU NO PREPAREDSTATEMENT?            ║
        ╠══════════════════════════════════════════════════════════════════════════════════╣
        ║ 1. O que o atacante tentou digitar:                                             ║
        ║    ' OR '1'='1' --                                                               ║
        ║                                                                                  ║
        ║ 2. O que o banco fez com essa entrada?                                           ║
        ║    Como a estrutura da consulta SQL já havia sido compilada antecipadamente,     ║
        ║    as aspas simples (') NÃO fecham nenhuma cláusula de comando!                  ║
        ║    O banco de dados procurou literalmente no disco por um usuário cujo campo     ║
        ║    login fosse EXATAMENTE a sequência de caracteres:                             ║
        ║    " ' OR '1'='1' -- "                                                           ║
        ║                                                                                  ║
        ║ 3. Como ninguém tem esse login bizarro cadastrado, o SELECT retornou ZERO        ║
        ║    linhas!                                                                       ║
        ║                                                                                  ║
        ║ 4. A Conclusão de Engenharia:                                                    ║
        ║    O ataque foi 100% neutralizado pela arquitetura do PreparedStatement,        ║
        ║    sem você precisar gastar tempo criando filtros manuais de texto ou regex!     ║
        ╚══════════════════════════════════════════════════════════════════════════════════╝
        """);
    }
}
