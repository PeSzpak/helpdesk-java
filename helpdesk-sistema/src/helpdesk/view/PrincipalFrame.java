package helpdesk.view;

import helpdesk.dao.ChamadoDAO;
import helpdesk.model.Chamado;
import helpdesk.util.EmailNotificacao;
import helpdesk.util.ExportadorCSV;
import helpdesk.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Janela principal do sistema. Concentra a interface gráfica (Swing) com:
 *  - Aba "Chamados": cadastro/edição + tabela + busca/filtro (CRUD completo).
 *  - Aba "Relatórios": indicadores e exportação CSV.
 *  - Aba "Sobre": informações do sistema.
 *
 * Toda a comunicação com o banco passa pelo ChamadoDAO.
 */
public class PrincipalFrame extends JFrame {

    private final ChamadoDAO dao = new ChamadoDAO();
    private final String usuarioLogado;

    // Componentes do formulário
    private final JTextField txtNumero = new JTextField();
    private final JTextField txtSolicitante = new JTextField();
    private final JComboBox<String> cbSetor =
            new JComboBox<>(new String[]{"TI", "Financeiro", "RH", "Comercial", "Operações"});
    private final JComboBox<String> cbPrioridade =
            new JComboBox<>(new String[]{Chamado.PRIORIDADE_BAIXA, Chamado.PRIORIDADE_MEDIA, Chamado.PRIORIDADE_ALTA});
    private final JComboBox<String> cbStatus =
            new JComboBox<>(new String[]{Chamado.STATUS_ABERTO, Chamado.STATUS_EM_ATENDIMENTO, Chamado.STATUS_FINALIZADO});
    private final JTextArea txtDescricao = new JTextArea(4, 20);
    private final JLabel lblDataAbertura = new JLabel("-");

    // Tabela
    private DefaultTableModel modeloTabela;
    private JTable tabela;

    // Busca / filtro
    private final JTextField txtBusca = new JTextField(18);
    private final JComboBox<String> cbFiltroStatus =
            new JComboBox<>(new String[]{"Todos", Chamado.STATUS_ABERTO, Chamado.STATUS_EM_ATENDIMENTO, Chamado.STATUS_FINALIZADO});

    // Indicadores (aba Relatórios)
    private final JLabel lblTotal = new JLabel();
    private final JLabel lblAbertos = new JLabel();
    private final JLabel lblAtendimento = new JLabel();
    private final JLabel lblFinalizados = new JLabel();
    private final JTextArea areaRelatorio = new JTextArea();

    // id do chamado selecionado (0 = nenhum / novo cadastro)
    private int idSelecionado = 0;

    public PrincipalFrame(String usuarioLogado) {
        super("Sistema de Gestão de Chamados - Help Desk");
        this.usuarioLogado = usuarioLogado;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080, 660);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(960, 600));

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(UITheme.FONTE_LABEL);
        abas.addTab("  Chamados  ", criarAbaChamados());
        abas.addTab("  Relatórios  ", criarAbaRelatorios());
        abas.addTab("  Sobre  ", criarAbaSobre());

        add(criarCabecalho(), BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);

        // Carga inicial dos dados
        sugerirNumero();
        atualizarTabela();
    }

    // ===================== CABEÇALHO =====================
    private JPanel criarCabecalho() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(UITheme.AZUL_ESCURO);
        topo.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel titulo = new JLabel("🎧  Help Desk — Gestão de Chamados de Suporte Técnico");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel usuario = new JLabel("Usuário: " + usuarioLogado + "   ");
        usuario.setForeground(new Color(0xB9C4DA));
        usuario.setFont(UITheme.FONTE_NORMAL);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(usuario, BorderLayout.EAST);
        return topo;
    }

    // ===================== ABA CHAMADOS =====================
    private JPanel criarAbaChamados() {
        JPanel painel = new JPanel(new BorderLayout(12, 12));
        painel.setBackground(UITheme.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        painel.add(criarFormulario(), BorderLayout.WEST);
        painel.add(criarPainelTabela(), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BRANCO);
        form.setBorder(UITheme.tituloBorda("Cadastro / Edição de Chamado"));
        form.setPreferredSize(new Dimension(360, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.weightx = 1;

        int y = 0;
        addCampo(form, gbc, y++, "Número:", txtNumero);
        addCampo(form, gbc, y++, "Solicitante:", txtSolicitante);
        addCampo(form, gbc, y++, "Setor:", cbSetor);
        addCampo(form, gbc, y++, "Prioridade:", cbPrioridade);
        addCampo(form, gbc, y++, "Status:", cbStatus);

        // Data de abertura (apenas leitura, preenchida automaticamente)
        gbc.gridy = y++;
        JPanel linhaData = new JPanel(new BorderLayout());
        linhaData.setBackground(UITheme.BRANCO);
        JLabel lbl = new JLabel("Data de Abertura:");
        lbl.setFont(UITheme.FONTE_LABEL);
        lblDataAbertura.setFont(UITheme.FONTE_NORMAL);
        lblDataAbertura.setForeground(UITheme.CINZA);
        linhaData.add(lbl, BorderLayout.NORTH);
        linhaData.add(lblDataAbertura, BorderLayout.CENTER);
        form.add(linhaData, gbc);

        // Descrição
        gbc.gridy = y++;
        JLabel lblDesc = new JLabel("Descrição do Problema:");
        lblDesc.setFont(UITheme.FONTE_LABEL);
        form.add(lblDesc, gbc);

        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setFont(UITheme.FONTE_NORMAL);
        form.add(new JScrollPane(txtDescricao), gbc);

        // Botões
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        form.add(criarPainelBotoes(), gbc);

        return form;
    }

    private void addCampo(JPanel form, GridBagConstraints gbc, int y, String rotulo, JComponent campo) {
        gbc.gridy = y;
        JPanel linha = new JPanel(new BorderLayout(0, 2));
        linha.setBackground(UITheme.BRANCO);
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(UITheme.FONTE_LABEL);
        campo.setFont(UITheme.FONTE_NORMAL);
        if (campo instanceof JTextField tf) tf.setColumns(18);
        linha.add(lbl, BorderLayout.NORTH);
        linha.add(campo, BorderLayout.CENTER);
        form.add(linha, gbc);
    }

    private JPanel criarPainelBotoes() {
        JPanel botoes = new JPanel(new GridLayout(2, 3, 6, 6));
        botoes.setBackground(UITheme.BRANCO);

        JButton btnNovo = UITheme.botao("Novo", UITheme.CINZA);
        JButton btnSalvar = UITheme.botao("Salvar", UITheme.VERDE);
        JButton btnAlterar = UITheme.botao("Alterar", UITheme.AZUL);
        JButton btnExcluir = UITheme.botao("Excluir", UITheme.VERMELHO);
        JButton btnLimpar = UITheme.botao("Limpar", UITheme.CINZA);

        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnAlterar);
        botoes.add(btnExcluir);
        botoes.add(btnLimpar);

        // Eventos (ActionListener)
        btnNovo.addActionListener(e -> novoCadastro());
        btnSalvar.addActionListener(e -> salvar());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        return botoes;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(UITheme.BRANCO);
        painel.setBorder(UITheme.tituloBorda("Chamados Cadastrados"));

        // Barra de busca / filtro
        JPanel barraBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        barraBusca.setBackground(UITheme.BRANCO);
        JLabel lblBusca = new JLabel("Pesquisar:");
        lblBusca.setFont(UITheme.FONTE_LABEL);
        txtBusca.setFont(UITheme.FONTE_NORMAL);
        JButton btnBuscar = UITheme.botao("🔍 Buscar", UITheme.AZUL);
        JButton btnLimparBusca = UITheme.botao("Limpar", UITheme.CINZA);

        barraBusca.add(lblBusca);
        barraBusca.add(txtBusca);
        barraBusca.add(new JLabel("Status:"));
        barraBusca.add(cbFiltroStatus);
        barraBusca.add(btnBuscar);
        barraBusca.add(btnLimparBusca);

        btnBuscar.addActionListener(e -> atualizarTabela());
        cbFiltroStatus.addActionListener(e -> atualizarTabela());
        txtBusca.addActionListener(e -> atualizarTabela()); // Enter dispara busca
        btnLimparBusca.addActionListener(e -> {
            txtBusca.setText("");
            cbFiltroStatus.setSelectedIndex(0);
            atualizarTabela();
        });

        // Tabela
        String[] colunas = {"ID", "Número", "Solicitante", "Setor", "Prioridade", "Data", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela apenas para leitura
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(26);
        tabela.setFont(UITheme.FONTE_NORMAL);
        tabela.getTableHeader().setFont(UITheme.FONTE_LABEL);
        tabela.setSelectionBackground(new Color(0xCFE0FF));
        tabela.getColumnModel().getColumn(0).setMaxWidth(40); // coluna ID estreita

        // Colore a célula de status
        tabela.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                                                           boolean foco, int linha, int col) {
                Component c = super.getTableCellRendererComponent(t, valor, sel, foco, linha, col);
                if (!sel) {
                    c.setForeground(UITheme.corDoStatus(String.valueOf(valor)));
                    setFont(UITheme.FONTE_LABEL);
                }
                return c;
            }
        });

        // Evento: ao clicar numa linha, carrega o chamado no formulário (ListSelectionListener)
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarSelecionado();
        });

        painel.add(barraBusca, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ===================== ABA RELATÓRIOS =====================
    private JPanel criarAbaRelatorios() {
        JPanel painel = new JPanel(new BorderLayout(12, 12));
        painel.setBackground(UITheme.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Cartões de indicadores
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setBackground(UITheme.FUNDO);
        cards.add(criarCard("Total de Chamados", lblTotal, UITheme.AZUL_ESCURO));
        cards.add(criarCard("Abertos", lblAbertos, UITheme.AZUL));
        cards.add(criarCard("Em Atendimento", lblAtendimento, UITheme.LARANJA));
        cards.add(criarCard("Finalizados", lblFinalizados, UITheme.VERDE));

        // Relatório textual
        areaRelatorio.setEditable(false);
        areaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaRelatorio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane sp = new JScrollPane(areaRelatorio);
        sp.setBorder(UITheme.tituloBorda("Relatório de Chamados"));

        // Botões de ação
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        acoes.setBackground(UITheme.FUNDO);
        JButton btnAtualizar = UITheme.botao("↻ Atualizar Indicadores", UITheme.AZUL);
        JButton btnCsv = UITheme.botao("⬇ Exportar CSV", UITheme.VERDE);
        acoes.add(btnAtualizar);
        acoes.add(btnCsv);

        btnAtualizar.addActionListener(e -> atualizarRelatorios());
        btnCsv.addActionListener(e -> exportarCsv());

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setBackground(UITheme.FUNDO);
        centro.add(cards, BorderLayout.NORTH);
        centro.add(sp, BorderLayout.CENTER);

        painel.add(centro, BorderLayout.CENTER);
        painel.add(acoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarCard(String titulo, JLabel valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cor);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel t = new JLabel(titulo);
        t.setForeground(Color.WHITE);
        t.setFont(UITheme.FONTE_LABEL);
        valor.setText("0");
        valor.setForeground(Color.WHITE);
        valor.setFont(new Font("SansSerif", Font.BOLD, 34));
        card.add(t, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);
        return card;
    }

    // ===================== ABA SOBRE =====================
    private JScrollPane criarAbaSobre() {
        JTextArea sobre = new JTextArea();
        sobre.setEditable(false);
        sobre.setFont(UITheme.FONTE_NORMAL);
        sobre.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        sobre.setText("""
                SISTEMA DE GESTÃO DE CHAMADOS DE SUPORTE TÉCNICO (HELP DESK)
                ============================================================

                Projeto Final Integrador — Análise e Projeto de Sistemas
                Faculdade Senac PR — Prof. Roberson Araujo

                Tecnologias utilizadas:
                  • Java (JDK 17) com interface gráfica Swing
                  • Persistência em banco de dados SQLite via JDBC
                  • Arquitetura em camadas (model, dao, util, view)

                Funcionalidades:
                  • Cadastro, consulta, alteração e exclusão de chamados (CRUD)
                  • Busca e filtros por texto e status
                  • Validações e tratamento de exceções
                  • Relatórios e indicadores
                  • Exportação para CSV e login de usuário

                Regras de negócio aplicadas:
                  • Campos obrigatórios não podem ficar vazios
                  • Número do chamado é único
                  • Prioridade: Baixa, Média ou Alta
                  • Status: Aberto, Em Atendimento ou Finalizado
                  • Chamados finalizados não podem ser excluídos
                  • Exclusões exigem confirmação
                """);
        return new JScrollPane(sobre);
    }

    // ===================== AÇÕES (CRUD) =====================

    /** Lê os campos da tela e monta um objeto Chamado. */
    private Chamado lerFormulario() {
        Chamado c = new Chamado();
        c.setId(idSelecionado);
        c.setNumero(txtNumero.getText().trim());
        c.setSolicitante(txtSolicitante.getText().trim());
        c.setSetor((String) cbSetor.getSelectedItem());
        c.setPrioridade((String) cbPrioridade.getSelectedItem());
        c.setStatus((String) cbStatus.getSelectedItem());
        c.setDescricao(txtDescricao.getText().trim());
        return c;
    }

    private void salvar() {
        Chamado c = lerFormulario();
        try {
            Validador.validarChamado(c); // validações + regras de negócio

            // Regra: número do chamado deve ser único
            if (dao.numeroJaExiste(c.getNumero(), 0)) {
                JOptionPane.showMessageDialog(this,
                        "Já existe um chamado com o número \"" + c.getNumero() + "\".",
                        "Número duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Data de abertura preenchida automaticamente
            c.setDataAbertura(agora());
            dao.inserir(c);

            JOptionPane.showMessageDialog(this,
                    "Chamado cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormulario();
            atualizarTabela();

        } catch (Validador.ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar no banco:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterar() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um chamado na tabela para alterar.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Chamado c = lerFormulario();
        try {
            Validador.validarChamado(c);

            if (dao.numeroJaExiste(c.getNumero(), idSelecionado)) {
                JOptionPane.showMessageDialog(this,
                        "Já existe outro chamado com o número \"" + c.getNumero() + "\".",
                        "Número duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dao.atualizar(c);

            // Desafio extra: notificação simulada ao finalizar
            if (c.isFinalizado()) {
                EmailNotificacao.notificarFinalizacao(c);
            }

            JOptionPane.showMessageDialog(this,
                    "Chamado atualizado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormulario();
            atualizarTabela();

        } catch (Validador.ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar no banco:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um chamado na tabela para excluir.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Regra: não permitir excluir chamado finalizado
        String statusAtual = (String) cbStatus.getSelectedItem();
        if (Chamado.STATUS_FINALIZADO.equals(statusAtual)) {
            JOptionPane.showMessageDialog(this,
                    "Chamados finalizados não podem ser excluídos.\n" +
                            "Apenas a alteração de status é permitida.",
                    "Operação não permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmação com JOptionPane
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o chamado \"" + txtNumero.getText() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION) {
            try {
                dao.excluir(idSelecionado);
                JOptionPane.showMessageDialog(this,
                        "Chamado excluído com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                atualizarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir no banco:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===================== APOIO DE INTERFACE =====================

    private void novoCadastro() {
        limparFormulario();
        sugerirNumero();
        txtSolicitante.requestFocus();
    }

    private void limparFormulario() {
        idSelecionado = 0;
        txtNumero.setText("");
        txtSolicitante.setText("");
        cbSetor.setSelectedIndex(0);
        cbPrioridade.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtDescricao.setText("");
        lblDataAbertura.setText("(preenchida ao salvar)");
        tabela.clearSelection();
    }

    private void sugerirNumero() {
        try {
            int ano = LocalDateTime.now().getYear();
            txtNumero.setText(dao.gerarProximoNumero(ano));
            lblDataAbertura.setText("(preenchida ao salvar)");
        } catch (SQLException ex) {
            txtNumero.setText("");
        }
    }

    /** Carrega os dados da linha selecionada na tabela para o formulário. */
    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;

        idSelecionado = (int) modeloTabela.getValueAt(linha, 0);
        txtNumero.setText(valor(linha, 1));
        txtSolicitante.setText(valor(linha, 2));
        cbSetor.setSelectedItem(valor(linha, 3));
        cbPrioridade.setSelectedItem(valor(linha, 4));
        lblDataAbertura.setText(valor(linha, 5));
        cbStatus.setSelectedItem(valor(linha, 6));

        // Busca a descrição completa no banco (não exibida na tabela)
        try {
            for (Chamado c : dao.listarTodos()) {
                if (c.getId() == idSelecionado) {
                    txtDescricao.setText(c.getDescricao());
                    break;
                }
            }
        } catch (SQLException ignored) { }
    }

    private String valor(int linha, int col) {
        Object o = modeloTabela.getValueAt(linha, col);
        return o == null ? "" : o.toString();
    }

    /** Recarrega a tabela aplicando os filtros de busca/status. */
    private void atualizarTabela() {
        try {
            String texto = txtBusca.getText();
            String status = (String) cbFiltroStatus.getSelectedItem();
            if ("Todos".equals(status)) status = null;

            List<Chamado> lista = dao.listarComFiltro(texto, status, null, null);

            modeloTabela.setRowCount(0);
            for (Chamado c : lista) {
                modeloTabela.addRow(new Object[]{
                        c.getId(), c.getNumero(), c.getSolicitante(), c.getSetor(),
                        c.getPrioridade(), c.getDataAbertura(), c.getStatus()
                });
            }
            atualizarRelatorios(); // mantém indicadores em sincronia
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar chamados:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== RELATÓRIOS =====================

    private void atualizarRelatorios() {
        try {
            Map<String, Integer> porStatus = dao.contarPorStatus();
            Map<String, Integer> porPrioridade = dao.contarPorPrioridade();

            int total = porStatus.values().stream().mapToInt(Integer::intValue).sum();
            lblTotal.setText(String.valueOf(total));
            lblAbertos.setText(String.valueOf(porStatus.getOrDefault(Chamado.STATUS_ABERTO, 0)));
            lblAtendimento.setText(String.valueOf(porStatus.getOrDefault(Chamado.STATUS_EM_ATENDIMENTO, 0)));
            lblFinalizados.setText(String.valueOf(porStatus.getOrDefault(Chamado.STATUS_FINALIZADO, 0)));

            StringBuilder sb = new StringBuilder();
            sb.append("RELATÓRIO GERAL DE CHAMADOS\n");
            sb.append("Gerado em: ").append(agora()).append("\n\n");
            sb.append("Total de chamados: ").append(total).append("\n\n");

            sb.append("── Por STATUS ───────────────────────\n");
            for (Map.Entry<String, Integer> e : porStatus.entrySet()) {
                sb.append(String.format("  %-18s %3d   %s%n",
                        e.getKey(), e.getValue(), barra(e.getValue(), total)));
            }

            sb.append("\n── Por PRIORIDADE ───────────────────\n");
            for (Map.Entry<String, Integer> e : porPrioridade.entrySet()) {
                sb.append(String.format("  %-18s %3d   %s%n",
                        e.getKey(), e.getValue(), barra(e.getValue(), total)));
            }
            areaRelatorio.setText(sb.toString());

        } catch (SQLException ex) {
            areaRelatorio.setText("Erro ao gerar relatório: " + ex.getMessage());
        }
    }

    /** Desenha uma barrinha proporcional simples para o relatório textual. */
    private String barra(int valor, int total) {
        if (total == 0) return "";
        int qtd = (int) Math.round((valor / (double) total) * 20);
        return "█".repeat(qtd);
    }

    private void exportarCsv() {
        try {
            List<Chamado> lista = dao.listarTodos();
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Não há chamados para exportar.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar relatório CSV");
            chooser.setSelectedFile(new File("chamados.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String caminho = chooser.getSelectedFile().getAbsolutePath();
                if (!caminho.toLowerCase().endsWith(".csv")) caminho += ".csv";
                ExportadorCSV.exportar(lista, caminho);
                JOptionPane.showMessageDialog(this,
                        "Arquivo exportado com sucesso:\n" + caminho,
                        "Exportação concluída", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar CSV:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String agora() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
