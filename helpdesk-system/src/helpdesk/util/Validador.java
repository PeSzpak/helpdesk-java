package helpdesk.util;

import helpdesk.model.Chamado;

/**
 * Centraliza as validações de dados e regras de negócio do formulário de chamados.
 *
 * Lança ValidacaoException (exceção própria) sempre que uma regra é violada,
 * permitindo que a interface gráfica trate tudo num único bloco try/catch.
 */
public class Validador {

    /** Exceção customizada de validação (tratamento de exceções). */
    public static class ValidacaoException extends Exception {
        public ValidacaoException(String mensagem) {
            super(mensagem);
        }
    }

    /**
     * Valida todos os campos obrigatórios e as regras de domínio de um chamado.
     * @throws ValidacaoException com mensagem amigável caso algo esteja inválido.
     */
    public static void validarChamado(Chamado c) throws ValidacaoException {
        // Regra: não permitir campos obrigatórios vazios.
        exigirPreenchido(c.getNumero(), "Número do chamado");
        exigirPreenchido(c.getSolicitante(), "Solicitante");
        exigirPreenchido(c.getSetor(), "Setor");
        exigirPreenchido(c.getDescricao(), "Descrição do problema");
        exigirPreenchido(c.getPrioridade(), "Prioridade");
        exigirPreenchido(c.getStatus(), "Status");

        // Regra: prioridade deve ser Baixa, Média ou Alta.
        if (!ehPrioridadeValida(c.getPrioridade())) {
            throw new ValidacaoException(
                    "Prioridade inválida. Use: Baixa, Média ou Alta.");
        }

        // Regra: status deve ser Aberto, Em Atendimento ou Finalizado.
        if (!ehStatusValido(c.getStatus())) {
            throw new ValidacaoException(
                    "Status inválido. Use: Aberto, Em Atendimento ou Finalizado.");
        }
    }

    private static void exigirPreenchido(String valor, String campo)
            throws ValidacaoException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        }
    }

    private static boolean ehPrioridadeValida(String p) {
        return Chamado.PRIORIDADE_BAIXA.equals(p)
                || Chamado.PRIORIDADE_MEDIA.equals(p)
                || Chamado.PRIORIDADE_ALTA.equals(p);
    }

    private static boolean ehStatusValido(String s) {
        return Chamado.STATUS_ABERTO.equals(s)
                || Chamado.STATUS_EM_ATENDIMENTO.equals(s)
                || Chamado.STATUS_FINALIZADO.equals(s);
    }
}
