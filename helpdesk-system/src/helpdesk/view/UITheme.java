package helpdesk.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/**
 * Paleta de cores e fábrica de componentes estilizados.
 * Mantém a aparência da aplicação consistente e agradável (usabilidade).
 */
public final class UITheme {

    private UITheme() { }

    // Paleta inspirada no material do projeto (azul-marinho + laranja Senac).
    public static final Color AZUL_ESCURO = new Color(0x1B2A4A);
    public static final Color AZUL        = new Color(0x2D5BD0);
    public static final Color LARANJA     = new Color(0xE2630E);
    public static final Color VERDE       = new Color(0x2E9E5B);
    public static final Color VERMELHO    = new Color(0xC0392B);
    public static final Color CINZA       = new Color(0x6C7A89);
    public static final Color FUNDO       = new Color(0xF4F6FA);
    public static final Color BRANCO      = Color.WHITE;

    public static final Font FONTE_TITULO  = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONTE_LABEL    = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONTE_NORMAL   = new Font("SansSerif", Font.PLAIN, 13);

    /**
     * Cria um botão colorido com texto branco, sem borda e com cursor de mão.
     */
    public static JButton botao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setFont(FONTE_LABEL);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    /**
     * Borda com título usada para agrupar áreas do formulário.
     */
    public static Border tituloBorda(String titulo) {
        Border linha = BorderFactory.createLineBorder(new Color(0xD0D7E2));
        Border comTitulo = BorderFactory.createTitledBorder(linha, titulo);
        Border espacamento = BorderFactory.createEmptyBorder(8, 10, 10, 10);
        return BorderFactory.createCompoundBorder(comTitulo, espacamento);
    }

    /**
     * Aplica uma cor de acordo com o status (usado para colorir indicadores).
     */
    public static Color corDoStatus(String status) {
        if (status == null) return CINZA;
        return switch (status) {
            case "Aberto" -> AZUL;
            case "Em Atendimento" -> LARANJA;
            case "Finalizado" -> VERDE;
            default -> CINZA;
        };
    }

    public static void padding(JComponent c, int v, int h) {
        c.setBorder(BorderFactory.createEmptyBorder(v, h, v, h));
    }
}
