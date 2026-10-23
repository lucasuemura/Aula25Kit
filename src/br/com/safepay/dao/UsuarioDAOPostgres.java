package br.com.safepay.dao;

import br.com.safepay.exception.PersistenciaException;
import br.com.safepay.factory.ConexaoFactory;
import br.com.safepay.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação concreta do DAO utilizando PreparedStatement (100% imune a SQL Injection),
 * fechamento determinístico via try-with-resources (prevenção de Connection Leaks)
 * e tradução de exceções (Exception Translation).
 */
public class UsuarioDAOPostgres implements UsuarioDAO {

    @Override
    public void inserir(Usuario usuario, String senha) {
        String sql = "INSERT INTO usuarios (nome, login, senha, perfil) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, senha);
            stmt.setString(4, usuario.getPerfil());

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                try (ResultSet chaves = stmt.getGeneratedKeys()) {
                    if (chaves.next()) {
                        usuario.setId(chaves.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[LOG JDBC] Código: " + e.getErrorCode() + " - " + e.getMessage());
            throw new PersistenciaException("Falha ao inserir usuário no banco de dados.", e);
        }
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, login = ?, perfil = ? WHERE id = ?";

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getPerfil());
            stmt.setInt(4, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[LOG JDBC] Código: " + e.getErrorCode() + " - " + e.getMessage());
            throw new PersistenciaException("Falha ao atualizar usuário no banco de dados.", e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[LOG JDBC] Código: " + e.getErrorCode() + " - " + e.getMessage());
            throw new PersistenciaException("Falha ao excluir usuário do banco de dados.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT id, nome, login, perfil FROM usuarios WHERE id = ?";

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLinha(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("[LOG JDBC] Código: " + e.getErrorCode() + " - " + e.getMessage());
            throw new PersistenciaException("Falha ao consultar usuário por ID.", e);
        }
    }

    @Override
    public Optional<Usuario> autenticar(String login, String senha) {
        String sql = "SELECT id, nome, login, perfil FROM usuarios WHERE login = ? AND senha = ?";

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLinha(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("[LOG JDBC] Código: " + e.getErrorCode() + " - " + e.getMessage());
            throw new PersistenciaException("Falha ao autenticar usuário.", e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nome, login, perfil FROM usuarios ORDER BY id";

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLinha(rs));
            }
        } catch (SQLException e) {
            System.err.println("[LOG JDBC] Código: " + e.getErrorCode() + " - " + e.getMessage());
            throw new PersistenciaException("Falha ao listar usuários.", e);
        }

        return lista;
    }

    private Usuario mapearLinha(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("login"),
                rs.getString("perfil")
        );
    }
}
