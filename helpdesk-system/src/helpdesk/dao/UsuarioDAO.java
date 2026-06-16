package helpdesk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO responsável pela autenticação de usuários (desafio extra: login).
 */
public class UsuarioDAO {

    /**
     * Verifica se existe um usuário com o login e senha informados.
     * @return true se as credenciais forem válidas.
     */
    public boolean autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE login = ? AND senha = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }
        }
    }
}
