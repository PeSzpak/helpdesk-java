package helpdesk;

import helpdesk.dao.ConexaoSQLite;
import helpdesk.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada da aplicação Help Desk.
 *
 * 1) Aplica o visual nativo do sistema operacional (Look and Feel).
 * 2) Inicializa o banco de dados (cria tabelas se necessário).
 * 3) Abre a tela de login na thread de eventos do Swing (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Aplica o Look and Feel do sistema para uma aparência mais nativa.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Não foi possível aplicar o tema do sistema: " + e.getMessage());
        }

        // Garante que as tabelas existam antes de abrir a interface.
        ConexaoSQLite.inicializarBanco();

        // Toda a UI Swing deve ser construída na Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
