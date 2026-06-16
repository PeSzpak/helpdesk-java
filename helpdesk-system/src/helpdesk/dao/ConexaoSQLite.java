package helpdesk.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Responsável por toda a comunicação de baixo nível com o banco SQLite via JDBC.
 *
 * - abre a conexão (Connection / DriverManager);
 * - garante que as tabelas existam (criando-as na primeira execução);
 * - cria um usuário padrão para o login do sistema.
 */
public class ConexaoSQLite {

    // Caminho do arquivo .db (criado automaticamente na primeira execução).
    private static final String URL = "jdbc:sqlite:helpdesk.db";

    /**
     * Abre e devolve uma conexão JDBC com o banco.
     * @throws SQLException caso o driver não esteja no classpath ou o caminho seja inválido.
     */
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Cria as tabelas do sistema caso ainda não existam.
     * É chamado uma única vez na inicialização da aplicação.
     */
    public static void inicializarBanco() {
        String sqlChamados = """
            CREATE TABLE IF NOT EXISTS chamados (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero TEXT UNIQUE NOT NULL,
                solicitante TEXT NOT NULL,
                setor TEXT NOT NULL,
                descricao TEXT NOT NULL,
                prioridade TEXT NOT NULL,
                data_abertura TEXT NOT NULL,
                status TEXT NOT NULL
            );
            """;

        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                login TEXT UNIQUE NOT NULL,
                senha TEXT NOT NULL
            );
            """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlChamados);
            stmt.execute(sqlUsuarios);

            // Cria o usuário padrão (admin / 1234) apenas se a tabela estiver vazia.
            String existeUsuario = "SELECT COUNT(*) AS total FROM usuarios";
            var rs = stmt.executeQuery(existeUsuario);
            if (rs.next() && rs.getInt("total") == 0) {
                stmt.execute("INSERT INTO usuarios (login, senha) VALUES ('admin', '1234')");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao inicializar o banco de dados: " + e.getMessage(), e);
        }
    }
}
