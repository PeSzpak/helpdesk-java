package helpdesk.model;

/**
 * Classe de modelo (POO) que representa um Chamado de suporte técnico.
 *
 * Cada atributo corresponde a uma coluna da tabela "chamados" no banco SQLite.
 * A classe é um POJO: encapsula os dados (atributos privados) e expõe o acesso
 * por meio de métodos getters e setters.
 */
public class Chamado {

    // ===== Constantes de domínio (regras de negócio) =====
    public static final String STATUS_ABERTO = "Aberto";
    public static final String STATUS_EM_ATENDIMENTO = "Em Atendimento";
    public static final String STATUS_FINALIZADO = "Finalizado";

    public static final String PRIORIDADE_BAIXA = "Baixa";
    public static final String PRIORIDADE_MEDIA = "Média";
    public static final String PRIORIDADE_ALTA = "Alta";

    // ===== Atributos (mapeiam as colunas da tabela) =====
    private int id;
    private String numero;
    private String solicitante;
    private String setor;
    private String descricao;
    private String prioridade;
    private String dataAbertura;
    private String status;

    // ===== Construtores =====
    public Chamado() {
    }

    public Chamado(String numero, String solicitante, String setor,
                   String descricao, String prioridade, String status) {
        this.numero = numero;
        this.solicitante = solicitante;
        this.setor = setor;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = status;
    }

    // ===== Getters e Setters (encapsulamento) =====
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(String dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Indica se o chamado já está finalizado.
     * Usado na regra de negócio que impede a exclusão de chamados finalizados.
     */
    public boolean isFinalizado() {
        return STATUS_FINALIZADO.equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return numero + " - " + solicitante + " (" + status + ")";
    }
}
