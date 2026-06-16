package helpdesk.util;

import helpdesk.model.Chamado;

/**
 * Notificação por e-mail SIMULADA (desafio extra).
 *
 * Em um sistema real aqui entraria a API JavaMail / SMTP. Para fins didáticos,
 * apenas registramos no console a mensagem que seria enviada quando um chamado
 * é marcado como "Finalizado".
 */
public class EmailNotificacao {

    public static void notificarFinalizacao(Chamado c) {
        System.out.println("====================================================");
        System.out.println(" [E-MAIL SIMULADO] Notificação de chamado finalizado");
        System.out.println(" Para: equipe-suporte@empresa.com");
        System.out.println(" Assunto: Chamado " + c.getNumero() + " finalizado");
        System.out.println(" ----------------------------------------------------");
        System.out.println(" Olá, o chamado abaixo foi marcado como FINALIZADO:");
        System.out.println("   Número.....: " + c.getNumero());
        System.out.println("   Solicitante: " + c.getSolicitante());
        System.out.println("   Setor......: " + c.getSetor());
        System.out.println("   Prioridade.: " + c.getPrioridade());
        System.out.println("====================================================");
    }
}
