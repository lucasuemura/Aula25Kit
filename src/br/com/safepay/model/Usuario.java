package br.com.safepay.model;

/**
 * ============================================================================
 * CLASSE DE MODELO (MODEL / POJO - Plain Old Java Object)
 * ============================================================================
 * Representa a entidade "Usuario" no nosso sistema bancário fictício SafePay.
 * 
 * Papel no MVC e na Persistência:
 * - Esta classe espelha as colunas da tabela "usuarios" do banco de dados relacional.
 * - Serve para transportar dados entre as consultas SQL e as regras de negócio Java.
 * ============================================================================
 */
public class Usuario {

    // 1. Identificador único no banco (Chave Primária / PRIMARY KEY AUTOINCREMENT)
    private int id;

    // 2. Nome de exibição do usuário (ex: "Administrador do Sistema")
    private String nome;

    // 3. Login de acesso (ex: "admin")
    private String login;

    // 4. Perfil de acesso / Cargo (ex: "DIRETOR", "ANALISTA", "CLIENTE")
    private String perfil;

    /**
     * Construtor padrão sem argumentos.
     * Útil para instanciações dinâmicas e frameworks.
     */
    public Usuario() {
    }

    /**
     * Construtor completo para quando já resgatamos todos os dados do banco de dados.
     * 
     * @param id     Identificador único gerado pelo banco.
     * @param nome   Nome completo do titular.
     * @param login  Credencial de login.
     * @param perfil Nível de autorização (ex: "DIRETOR").
     */
    public Usuario(int id, String nome, String login, String perfil) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.perfil = perfil;
    }

    // ============================================================================
    // GETTERS E SETTERS (Encapsulamento POO)
    // ============================================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    /**
     * Sobrescrita amigável do toString() para exibir os dados no console de forma legível.
     */
    @Override
    public String toString() {
        return String.format("Usuario [ID: %d | Nome: %s | Login: %s | Perfil: %s]",
                id, nome, login, perfil);
    }
}
