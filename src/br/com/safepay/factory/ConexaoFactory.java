package br.com.safepay.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ============================================================================
 * PADRÃO DE PROJETO GoF: FACTORY (FÁBRICA DE CONEXÕES)
 * ============================================================================
 * 
 * ❓ POR QUE CRIAR UMA "ConexaoFactory"?
 * 
 * 1. PONTO ÚNICO DE MANUTENÇÃO (Centralização):
 *    Se você espalhar a URL "jdbc:sqlite:safepay.db" em 20 telas e 30 DAOs diferentes,
 *    no dia em que o banco mudar de nome, de pasta ou migrar para PostgreSQL na nuvem,
 *    você terá que caçar e alterar 50 arquivos no projeto.
 *    Na ConexaoFactory, você altera em UMA ÚNICA LINHA (na constante URL) e todo
 *    o sistema se atualiza automaticamente!
 * 
 * 2. PRINCÍPIO DA RESPONSABILIDADE ÚNICA (SOLID - SRP):
 *    A tela cuida de desenhar botões. O modelo cuida das regras de negócio.
 *    Quem cuida da infraestrutura de bater na porta do banco e abrir o canal
 *    de comunicação é exclusivamente esta classe.
 * 
 * 3. DESACOPLAMENTO E EVOLUÇÃO:
 *    Se no futuro a empresa decidir usar um "Pool de Conexões de Alta Performance"
 *    (como HikariCP ou Apache DBCP), alteramos apenas o corpo de obterConexao().
 *    O restante do sistema continuará chamando ConexaoFactory.obterConexao()
 *    sem quebrar absolutamente nada!
 * ============================================================================
 */
public final class ConexaoFactory {

    /**
     * URL de Conexão JDBC (O "endereço web" do nosso banco):
     * 
     * Estrutura da URL:
     * - "jdbc:"    -> Informa à JVM que esta string deve ser tratada pelo subsistema JDBC.
     * - "sqlite:"  -> Subprotocolo que identifica o driver SQLite da pasta lib/ (sqlite-jdbc.jar).
     * - "safepay.db" -> Nome do arquivo físico no disco rígido do computador.
     * 
     * 💡 COMPORTAMENTO INTELIGENTE DO SQLITE:
     * Se o arquivo "safepay.db" ainda NÃO existir na pasta do projeto, o SQLite
     * cria o arquivo vazio no mesmo milissegundo em que a conexão é solicitada!
     */
    private static final String URL = "jdbc:sqlite:safepay.db";

    /**
     * ❓ POR QUE O CONSTRUTOR É PRIVADO?
     * 
     * Esta é uma "Classe Utilitária". Ela só possui métodos e constantes estáticas.
     * Não faz nenhum sentido um programador fazer "new ConexaoFactory()".
     * Bloquear o construtor como 'private' protege o código contra instanciações
     * acidentais na memória RAM e força o uso correto: ConexaoFactory.obterConexao().
     */
    private ConexaoFactory() {
        throw new UnsupportedOperationException("ConexaoFactory e uma classe utilitaria e nao deve ser instanciada!");
    }

    /**
     * Entrega uma nova conexão ativa pronta para uso.
     * 
     * ❓ PRECISO USAR Class.forName("org.sqlite.JDBC")?
     * Resposta: NÃO! Desde o Java 6 (especificação JDBC 4.0 lançada em 2006), o Java
     * utiliza o mecanismo SPI (Service Provider Interface). Ao colocar o arquivo
     * .jar do driver na pasta lib/ (ou nas bibliotecas do VS Code), a JVM descobre
     * e registra o driver automaticamente em segundo plano!
     * 
     * @return Um objeto do tipo {@link Connection} (a linha telefônica aberta com o banco).
     * @throws SQLException Se o arquivo do banco estiver bloqueado, sem permissão ou o driver ausente.
     */
    public static Connection obterConexao() throws SQLException {
        // DriverManager é o despachante oficial do Java que localiza o driver
        // e estabelece a sessão de comunicação com o arquivo no disco rígido.
        return DriverManager.getConnection(URL);
    }
}
