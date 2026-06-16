import java.sql.*;

public class Main {

    public static void main(String[] args) {
        // try-with-resources: a conexão é fechada automaticamente ao final.
        try (Connection conn =
                 DriverManager.getConnection("jdbc:sqlite:produtos.db")) {

            System.out.println("Conectado!");

        } catch (SQLException e) {
            // Se algo falhar (driver ausente, caminho inválido etc.),
            // mostramos a mensagem da exceção para ajudar no diagnóstico.
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }
}
