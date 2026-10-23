package br.com.safepay.main;

import br.com.safepay.dao.UsuarioDAO;
import br.com.safepay.dao.UsuarioDAOPostgres;
import br.com.safepay.db.InicializadorBanco;
import br.com.safepay.exception.PersistenciaException;
import br.com.safepay.model.Usuario;
import br.com.safepay.seguranca.DemonstracaoSeguraPreparedStatement;
import br.com.safepay.seguranca.DemonstracaoVulneravelStatement;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * ============================================================================
 * APLICAÇÃO PRINCIPAL: LABORATÓRIO DE PERSISTÊNCIA & SEGURANÇA (AULA 25)
 * ============================================================================
 */
public class MainApp {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("💳 SafePay FinTech • Sistema de Autenticação & Bancada JDBC");
        System.out.println("   Disciplina: Programação Orientada a Objetos (Aula 25 / Semana 13)");
        System.out.println("   Prof. Mestre Júlio B. Herculani • UniCesumar");
        System.out.println("====================================================================");

        // 1. Inicializa a tabela e garante os usuários de teste no SQLite
        InicializadorBanco.inicializar();

        UsuarioDAO usuarioDAO = new UsuarioDAOPostgres();
        Scanner scanner = new Scanner(System.in);
        boolean executando = true;

        while (executando) {
            System.out.println("\n--------------------------------------------------------------------");
            System.out.println("ESCOLHA UMA OPÇÃO PARA EXPERIMENTAR NA BANCADA:");
            System.out.println(" [1] Testar Login Normal Legítimo (usuário: mariana | senha: financas123)");
            System.out.println(" [2] Simular Ataque de SQL Injection (Modo Vulnerável - Statement)");
            System.out.println(" [3] Testar o Mesmo Ataque no Modo Seguro (PreparedStatement / DAO)");
            System.out.println(" [4] Listar Todos os Usuários Gravados no Banco via UsuarioDAO");
            System.out.println(" [5] Digitar Login e Senha Manuais (Modo Livre)");
            System.out.println(" [6] Cadastrar Novo Usuário via UsuarioDAO (PreparedStatement)");
            System.out.println(" [0] Sair");
            System.out.print("👉 Digite a sua opção: ");

            String opcao = scanner.nextLine().trim();

            try {
                switch (opcao) {
                    case "1" -> executarLoginLegitimo(usuarioDAO);
                    case "2" -> executarAtaqueSqlInjection();
                    case "3" -> executarDefesaPreparedStatement(usuarioDAO);
                    case "4" -> listarUsuariosBanco(usuarioDAO);
                    case "5" -> executarTesteLivre(scanner, usuarioDAO);
                    case "6" -> cadastrarNovoUsuario(scanner, usuarioDAO);
                    case "0" -> {
                        System.out.println("\n👋 Encerrando o laboratório da SafePay. Até a próxima aula!");
                        executando = false;
                    }
                    default -> System.out.println("⚠️ Opção inválida! Escolha um número de 0 a 6.");
                }
            } catch (PersistenciaException e) {
                System.out.println("⚠️ Erro de persistência tratado com segurança: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void executarLoginLegitimo(UsuarioDAO dao) {
        System.out.println("\n--- [TESTE 1] AUTENTICAÇÃO COM DADOS REAIS ---");
        String login = "mariana";
        String senha = "financas123";

        System.out.println("Tentando autenticar como '" + login + "'...");
        Optional<Usuario> uOpt = dao.autenticar(login, senha);

        if (uOpt.isPresent()) {
            System.out.println("🎉 SUCESSO! Usuário autenticado:");
            System.out.println("   -> " + uOpt.get());
        } else {
            System.out.println("❌ Falha na autenticação!");
        }
    }

    private static void executarAtaqueSqlInjection() {
        System.out.println("\n--- [TESTE 2] ATAQUE SQL INJECTION NO CÓDIGO VULNERÁVEL ---");
        String payloadAtaque = "' OR '1'='1' --";
        String senhaQualquer = "qualquer_coisa_errada";

        System.out.println("Injetando payload no campo de Login: " + payloadAtaque);
        System.out.println("Senha informada: " + senhaQualquer);

        Usuario invasor = DemonstracaoVulneravelStatement.autenticarComFalha(payloadAtaque, senhaQualquer);

        if (invasor != null) {
            System.out.println("\n🚨 PERIGO MÁXIMO! O ATAQUE FUNCIONOU!");
            System.out.println("   O invasor NÃO sabia a senha e conseguiu logar como:");
            System.out.println("   👤 " + invasor.getNome() + " [Cargo: " + invasor.getPerfil() + "]");
            System.out.println("   🔐 Acesso irrestrito concedido aos cofres da SafePay!");
            DemonstracaoVulneravelStatement.explicarGolpe();
        } else {
            System.out.println("O login foi recusado.");
        }
    }

    private static void executarDefesaPreparedStatement(UsuarioDAO dao) {
        System.out.println("\n--- [TESTE 3] MESMO ATAQUE CONTRA O CÓDIGO SEGURO ---");
        String payloadAtaque = "' OR '1'='1' --";
        String senhaQualquer = "qualquer_coisa_errada";

        System.out.println("Tentando injetar no PreparedStatement / DAO: " + payloadAtaque);

        Usuario usuario = DemonstracaoSeguraPreparedStatement.autenticarComSeguranca(payloadAtaque, senhaQualquer);
        Optional<Usuario> daoResult = dao.autenticar(payloadAtaque, senhaQualquer);

        if (usuario != null || daoResult.isPresent()) {
            System.out.println("🚨 Inesperado: o sistema aceitou o login!");
        } else {
            System.out.println("\n🛡️ BLOQUEADO COM SUCESSO!");
            System.out.println("   O banco de dados tratou o texto como dado puro.");
            System.out.println("   Nenhum usuário foi retornado. O sistema permaneceu 100% seguro!");
            DemonstracaoSeguraPreparedStatement.explicarDefesa();
        }
    }

    private static void listarUsuariosBanco(UsuarioDAO dao) {
        System.out.println("\n--- [TESTE 4] TABELA DE USUÁRIOS NO BANCO SQLITE (safepay.db) ---");
        List<Usuario> usuarios = dao.listarTodos();

        System.out.printf("%-4s | %-32s | %-12s | %-15s\n", "ID", "NOME", "LOGIN", "PERFIL");
        System.out.println("------------------------------------------------------------------------");

        for (Usuario u : usuarios) {
            System.out.printf("%-4d | %-32s | %-12s | %-15s\n",
                    u.getId(),
                    u.getNome(),
                    u.getLogin(),
                    u.getPerfil());
        }
    }

    private static void executarTesteLivre(Scanner scanner, UsuarioDAO dao) {
        System.out.println("\n--- [TESTE 5] MODO LIVRE DE EXPERIMENTAÇÃO ---");
        System.out.print("Digite o LOGIN a ser testado: ");
        String login = scanner.nextLine();

        System.out.print("Digite a SENHA a ser testada: ");
        String senha = scanner.nextLine();

        System.out.println("\nQual método você quer testar?");
        System.out.println("  [V] Vulnerável (Statement concatenado)");
        System.out.println("  [S] Seguro (PreparedStatement / UsuarioDAO)");
        System.out.print("Escolha (V/S): ");
        String escolha = scanner.nextLine().trim().toUpperCase();

        if (escolha.equals("V")) {
            Usuario u = DemonstracaoVulneravelStatement.autenticarComFalha(login, senha);
            System.out.println(u != null ? "✅ Logado com sucesso como: " + u.getNome() : "❌ Login inválido.");
        } else {
            Optional<Usuario> uOpt = dao.autenticar(login, senha);
            System.out.println(uOpt.isPresent() ? "✅ Logado com sucesso como: " + uOpt.get().getNome() : "❌ Login inválido.");
        }
    }

    private static void cadastrarNovoUsuario(Scanner scanner, UsuarioDAO dao) {
        System.out.println("\n--- [TESTE 6] CADASTRAR NOVO USUÁRIO COM SEGURANÇA ---");
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Login: ");
        String login = scanner.nextLine().trim();

        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        System.out.print("Perfil (ex: ANALISTA, CLIENTE): ");
        String perfil = scanner.nextLine().trim().toUpperCase();

        Usuario novo = new Usuario(0, nome, login, perfil);
        dao.inserir(novo, senha);
        System.out.println("✅ Usuário cadastrado com sucesso! ID gerado: " + novo.getId());
    }
}
