package helpdesk.util;

import helpdesk.model.Chamado;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Utilitário de manipulação de arquivos (desafio extra): exporta a lista de
 * chamados para um arquivo .csv que pode ser aberto no Excel.
 */
public class ExportadorCSV {

    /**
     * Escreve a lista de chamados no arquivo indicado, no formato CSV.
     * @throws IOException caso ocorra erro de escrita em disco.
     */
    public static void exportar(List<Chamado> chamados, String caminhoArquivo) throws IOException {
        // try-with-resources garante o fechamento do arquivo (manipulação de arquivos).
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
            // Cabeçalho
            writer.println("Numero;Solicitante;Setor;Prioridade;Data Abertura;Status;Descricao");

            // Linhas
            for (Chamado c : chamados) {
                writer.println(String.join(";",
                        seguro(c.getNumero()),
                        seguro(c.getSolicitante()),
                        seguro(c.getSetor()),
                        seguro(c.getPrioridade()),
                        seguro(c.getDataAbertura()),
                        seguro(c.getStatus()),
                        seguro(c.getDescricao())));
            }
        }
    }

    /**
     * Remove o separador ";" e quebras de linha para não corromper o CSV.
     */
    private static String seguro(String valor) {
        if (valor == null) return "";
        return valor.replace(";", ",").replace("\n", " ").replace("\r", " ").trim();
    }
}
