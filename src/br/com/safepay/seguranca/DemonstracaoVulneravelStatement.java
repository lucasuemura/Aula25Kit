package br.com.safepay.seguranca;

import br.com.safepay.factory.ConexaoFactory;
import br.com.safepay.model.Usuario;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ============================================================================
 * DEMONSTRAÇÃO DE CÓDIGO VULNERÁVEL (ANTI-PATTERN / INSEGURO)
 * ============================================================================
 * 
 * ⚠️ ATENÇÃO PEDAGÓGICA:
 * ESTE CÓDIGO CONTÉM UMA FALHA GRAVE DE SEGURANÇA CONHECIDA COMO "SQL INJECTION".
 * ELE FOI CRIADO EXCLUSIVAMENTE PARA FINS DIDÁTICOS DE DEMONSTRAÇÃO EM SALA!
 * NUNCA UTILIZE CONCATENAÇÃO DE STRINGS COM STATEMENT EM SISTEMAS DE PRODUÇÃO!
 * ============================================================================
 */
public class DemonstracaoVulneravelStatement {

    /**
     * Tenta autenticar um usuário no banco usando concatenação com o operador '+'
     * e o objeto Statement comum.
     * 
     * @param login Informado pelo usuário na tela.
     * @param senha Informada pelo usuário na tela.
     * @return O objeto Usuario se autenticado com sucesso, ou null se falhar.
     */
    public static Usuario autenticarComFalha(String login, String senha) {
        /*
         * ❌ O ERRO DO DESENVOLVEDOR INEXPERIENTE:
         * 
         * Aqui nós estamos montando a query SQL colando pedaços de texto com o operador '+'.
         * O banco de dados receberá este texto bruto sem conseguir distinguir onde termina
         * o código SQL do sistema e onde começam os dados digitados pelo usuário.
         */
        String sql = "SELECT id, nome, login, perfil FROM usuarios WHERE login = '" + login + "' AND senha = '" + senha + "'";

        System.out.println("\n--------------------------------------------------");
        System.out.println("🚨 [MODO VULNERÁVEL - STATEMENT CONCATENADO]");
        System.out.println("📜 Query enviada literalmente ao banco de dados:");
        System.out.println("👉 " + sql);
        System.out.println("--------------------------------------------------");

        /*
         * Usamos o try-with-resources para garantir que a conexão e o statement
         * sejam fechados, mas isso NÃO nos protege contra a injeção lógica do SQL!
         */
        try (Connection conn = ConexaoFactory.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            /*
             * rs.next(): Se o banco devolveu pelo menos uma linha que satisfez a condição
             * da cláusula WHERE, o usuário é considerado autenticado!
             */
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

        } catch (SQLException e) {
            System.err.println("❌ Erro de execução SQL: " + e.getMessage());
            return null;
        }
    }

    /**
     * Explicação pedagógica detalhada do golpe do ' OR '1'='1' --
     */
    public static void explicarGolpe() {
        System.out.println("""
        
        ╔══════════════════════════════════════════════════════════════════════════════════╗
        ║              ANATOMIA DO ATAQUE: COMO O INVASOR LOGOU SEM SABER A SENHA?          ║
        ╠══════════════════════════════════════════════════════════════════════════════════╣
        ║ 1. O que o atacante digitou no campo Login:                                     ║
        ║    ' OR '1'='1' --                                                               ║
        ║                                                                                  ║
        ║ 2. O que a concatenação colou no banco:                                          ║
        ║    SELECT * FROM usuarios WHERE login = '' OR '1'='1' --' AND senha = '...'       ║
        ║                                                                                  ║
        ║ 3. Por que o banco aceitou?                                                      ║
        ║    a) A primeira aspa (') fecha o valor do login antecipadamente: login = ''     ║
        ║    b) O operador OR adiciona uma verdade matemática: '1'='1' sempre é VERDADEIRO ║
        ║    c) Os dois traços (--) transformam todo o resto da linha (inclusive a senha)  ║
        ║       em um COMENTÁRIO ignorado pelo interpretador SQL do banco!                 ║
        ║                                                                                  ║
        ║ 4. O Resultado Trágico:                                                          ║
        ║    A condição se tornou verdadeira para TODAS as linhas da tabela. O banco       ║
        ║    devolveu a primeira linha encontrada: o Carlos Drummond (Diretor de TI)!      ║
        ╚══════════════════════════════════════════════════════════════════════════════════╝
        """);
    }
}
