package helpdesk.dao;

import helpdesk.model.Chamado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO (Data Access Object) — concentra todas as operações de banco relativas
 * aos Chamados. A interface gráfica nunca fala SQL diretamente: sempre passa por aqui.
 *
 * Usa PreparedStatement (proteção contra SQL Injection) e ResultSet para leitura.
 * As coleções retornadas são ArrayList (requisito de "Coleções para manipulação dos dados").
 */
public class ChamadoDAO {

    /**
     * Insere um novo chamado no banco.
     */
    public void inserir(Chamado c) throws SQLException {
        String sql = "INSERT INTO chamados " +
                "(numero, solicitante, setor, descricao, prioridade, data_abertura, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNumero());
            stmt.setString(2, c.getSolicitante());
            stmt.setString(3, c.getSetor());
            stmt.setString(4, c.getDescricao());
            stmt.setString(5, c.getPrioridade());
            stmt.setString(6, c.getDataAbertura());
            stmt.setString(7, c.getStatus());
            stmt.executeUpdate();
        }
    }

    /**
     * Atualiza um chamado existente (identificado pelo id).
     */
    public void atualizar(Chamado c) throws SQLException {
        String sql = "UPDATE chamados SET " +
                "numero = ?, solicitante = ?, setor = ?, descricao = ?, " +
                "prioridade = ?, status = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNumero());
            stmt.setString(2, c.getSolicitante());
            stmt.setString(3, c.getSetor());
            stmt.setString(4, c.getDescricao());
            stmt.setString(5, c.getPrioridade());
            stmt.setString(6, c.getStatus());
            stmt.setInt(7, c.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Exclui um chamado pelo id.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM chamados WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Lista todos os chamados cadastrados, ordenados pelo id.
     */
    public List<Chamado> listarTodos() throws SQLException {
        return listarComFiltro(null, null, null, null);
    }

    /**
     * Lista chamados aplicando filtros opcionais. Qualquer parâmetro nulo/vazio é ignorado.
     *
     * @param texto       busca por número, solicitante ou descrição (LIKE)
     * @param status      filtra por status exato (ou null para todos)
     * @param prioridade  filtra por prioridade exata (ou null para todas)
     * @param setor       filtra por setor exato (ou null para todos)
     */
    public List<Chamado> listarComFiltro(String texto, String status,
                                         String prioridade, String setor) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM chamados WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();

        if (texto != null && !texto.isBlank()) {
            sql.append(" AND (numero LIKE ? OR solicitante LIKE ? OR descricao LIKE ?)");
            String like = "%" + texto.trim() + "%";
            parametros.add(like);
            parametros.add(like);
            parametros.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            parametros.add(status);
        }
        if (prioridade != null && !prioridade.isBlank()) {
            sql.append(" AND prioridade = ?");
            parametros.add(prioridade);
        }
        if (setor != null && !setor.isBlank()) {
            sql.append(" AND setor = ?");
            parametros.add(setor);
        }
        sql.append(" ORDER BY id");

        List<Chamado> lista = new ArrayList<>();
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarChamado(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Verifica se já existe um chamado com o número informado.
     * @param idIgnorar id que deve ser desconsiderado na checagem (usado na edição).
     */
    public boolean numeroJaExiste(String numero, int idIgnorar) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM chamados WHERE numero = ? AND id <> ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            stmt.setInt(2, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }
        }
    }

    /**
     * Gera um número sugerido para o próximo chamado no formato CH-AAAA-NNN.
     */
    public String gerarProximoNumero(int ano) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM chamados";
        int sequencial = 1;
        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                sequencial = rs.getInt("total") + 1;
            }
        }
        return String.format("CH-%d-%03d", ano, sequencial);
    }

    // ===== Relatórios / indicadores =====

    /**
     * Conta os chamados agrupados por status. Retorna um mapa Status -> quantidade.
     */
    public Map<String, Integer> contarPorStatus() throws SQLException {
        return contarAgrupado("status");
    }

    /**
     * Conta os chamados agrupados por prioridade.
     */
    public Map<String, Integer> contarPorPrioridade() throws SQLException {
        return contarAgrupado("prioridade");
    }

    private Map<String, Integer> contarAgrupado(String coluna) throws SQLException {
        // coluna é controlada internamente (nunca vem do usuário), então é seguro.
        String sql = "SELECT " + coluna + " AS chave, COUNT(*) AS total " +
                "FROM chamados GROUP BY " + coluna + " ORDER BY total DESC";
        Map<String, Integer> resultado = new LinkedHashMap<>();
        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultado.put(rs.getString("chave"), rs.getInt("total"));
            }
        }
        return resultado;
    }

    /**
     * Converte uma linha do ResultSet em um objeto Chamado.
     */
    private Chamado montarChamado(ResultSet rs) throws SQLException {
        Chamado c = new Chamado();
        c.setId(rs.getInt("id"));
        c.setNumero(rs.getString("numero"));
        c.setSolicitante(rs.getString("solicitante"));
        c.setSetor(rs.getString("setor"));
        c.setDescricao(rs.getString("descricao"));
        c.setPrioridade(rs.getString("prioridade"));
        c.setDataAbertura(rs.getString("data_abertura"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}
