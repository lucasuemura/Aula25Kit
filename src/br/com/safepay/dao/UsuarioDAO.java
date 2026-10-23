package br.com.safepay.dao;

import br.com.safepay.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interface canônica do Padrão DAO (Data Access Object) para a entidade Usuario.
 * Garante o Princípio da Inversão de Dependência (DIP) e isolamento da camada de persistência.
 */
public interface UsuarioDAO {
    void inserir(Usuario usuario, String senha);
    void atualizar(Usuario usuario);
    void excluir(int id);
    Optional<Usuario> buscarPorId(int id);
    Optional<Usuario> autenticar(String login, String senha);
    List<Usuario> listarTodos();
}
