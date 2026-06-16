package helpdesk.view;

import helpdesk.dao.UsuarioDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Tela de login (desafio extra). Valida usuário/senha contra a tabela "usuarios"
 * e, em caso de sucesso, abre a janela principal do sistema.
 *
 * Usuário padrão criado automaticamente: login = admin / senha = 1234
 */
public class LoginFrame extends JFrame {

    private final JTextField campoLogin = new JTextField(15);
    private final JPasswordField campoSenha = new JPasswordField(15);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginFrame() {
        super("Help Desk - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);
        montarTela();
    }

    private void montarTela() {
        // Painel topo (cabeçalho azul-marinho)
        JPanel topo = new JPanel();
        topo.setBackground(UITheme.AZUL_ESCURO);
        topo.setPreferredSize(new Dimension(420, 110));
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("🎧  Sistema de Gestão de Chamados");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Help Desk - Suporte Técnico");
        sub.setForeground(new Color(0xB9C4DA));
        sub.setFont(UITheme.FONTE_NORMAL);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        topo.add(Box.createVerticalGlue());
        topo.add(titulo);
        topo.add(Box.createVerticalStrut(6));
        topo.add(sub);
        topo.add(Box.createVerticalGlue());

        // Painel central com formulário
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(UITheme.FUNDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblLogin = new JLabel("Usuário:");
        lblLogin.setFont(UITheme.FONTE_LABEL);
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(UITheme.FONTE_LABEL);

        gbc.gridx = 0; gbc.gridy = 0;
        centro.add(lblLogin, gbc);
        gbc.gridx = 1;
        centro.add(campoLogin, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        centro.add(lblSenha, gbc);
        gbc.gridx = 1;
        centro.add(campoSenha, gbc);

        JButton btnEntrar = UITheme.botao("Entrar", UITheme.VERDE);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centro.add(btnEntrar, gbc);

        JLabel dica = new JLabel("Padrão:  admin / 1234");
        dica.setForeground(UITheme.CINZA);
        dica.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridx = 1; gbc.gridy = 3;
        centro.add(dica, gbc);

        add(topo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        // Eventos: clique no botão e tecla Enter
        btnEntrar.addActionListener(e -> autenticar());
        getRootPane().setDefaultButton(btnEntrar);
    }

    private void autenticar() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe usuário e senha.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (usuarioDAO.autenticar(login, senha)) {
                dispose();
                PrincipalFrame principal = new PrincipalFrame(login);
                principal.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuário ou senha inválidos.",
                        "Falha no login", JOptionPane.ERROR_MESSAGE);
                campoSenha.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao acessar o banco de dados:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
