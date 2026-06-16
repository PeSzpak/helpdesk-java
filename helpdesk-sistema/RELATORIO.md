# Relatório do Projeto — Sistema de Gestão de Chamados (Help Desk)

**Disciplina:** Análise e Projeto de Sistemas — Faculdade Senac PR
**Professor:** Roberson Araujo
**Tecnologias:** Java 17 · Swing · JDBC · SQLite

---

## 1. Estrutura do sistema

O projeto foi organizado em **arquitetura em camadas**, separando responsabilidades:

- **`model`** — Representa os dados do mundo real (`Chamado`, `Usuario`) como objetos Java (POO).
- **`dao`** (Data Access Object) — Isola todo o acesso ao banco. A interface gráfica nunca escreve SQL diretamente; ela conversa apenas com o DAO.
  - `ConexaoSQLite` abre a conexão JDBC e cria as tabelas na primeira execução.
  - `ChamadoDAO` faz o CRUD e os relatórios.
  - `UsuarioDAO` cuida da autenticação.
- **`util`** — Funções de apoio reutilizáveis: validações (`Validador`), exportação de arquivo (`ExportadorCSV`) e notificação simulada (`EmailNotificacao`).
- **`view`** — A interface gráfica em Swing (`LoginFrame`, `PrincipalFrame`) e o tema visual (`UITheme`).
- **`Main`** — Inicializa o banco e abre a tela de login.

O fluxo de dados segue exatamente o modelo da atividade:
**Usuário → Interface Swing → Lógica/Validações → JDBC → Banco SQLite.**

A tabela principal `chamados` foi criada conforme o enunciado, com os campos
`id, numero, solicitante, setor, descricao, prioridade, data_abertura, status`.

---

## 2. Principais decisões

1. **Camadas separadas (DAO):** facilita a manutenção e deixa o código mais limpo,
   além de evitar repetição de SQL espalhada pela tela.
2. **`PreparedStatement` em todas as consultas:** protege contra SQL Injection e
   trata corretamente valores com acentos e caracteres especiais.
3. **Validação centralizada com exceção própria (`ValidacaoException`):**
   todas as regras de negócio ficam num só lugar e a tela trata tudo com um único `try/catch`.
4. **Número do chamado sugerido automaticamente** (formato `CH-AAAA-NNN`) e **data
   preenchida pelo sistema**, reduzindo erros do usuário.
5. **Interface com abas (`JTabbedPane`)** separando Chamados, Relatórios e Sobre,
   deixando a navegação organizada e profissional.
6. **Tema visual próprio (`UITheme`)** com a paleta do Senac (azul-marinho + laranja),
   botões coloridos por ação (verde=salvar, azul=alterar, vermelho=excluir) e status
   coloridos na tabela para melhorar a usabilidade.
7. **Driver SQLite incluído na pasta `lib/`** e scripts `run.sh`/`run.bat` para que o
   projeto rode em qualquer máquina com Java, sem precisar de Maven.

---

## 3. Dificuldades encontradas

- **Configuração do classpath do JDBC:** foi necessário apontar o driver `sqlite-jdbc.jar`
  no momento de compilar e executar. Resolvido com os scripts `run.sh`/`run.bat`.
- **Sincronizar a tabela com o formulário:** ao clicar numa linha, era preciso recarregar
  os dados (inclusive a descrição, que não aparece na tabela). Resolvido com o
  `ListSelectionListener` e uma busca pelo `id` selecionado.
- **Manter os indicadores atualizados:** os relatórios precisavam refletir cada inclusão,
  alteração ou exclusão. Resolvido chamando `atualizarRelatorios()` sempre que a tabela é recarregada.
- **Regra de exclusão de finalizados:** foi preciso bloquear o `DELETE` quando o status é
  "Finalizado", permitindo somente a mudança de status.
- **Codificação de caracteres (acentos):** ajustado com `-encoding UTF-8` na compilação.

---

## 4. Conclusão

O sistema atende a todos os requisitos obrigatórios (CRUD completo, JDBC, Swing,
validações, regras de negócio, relatórios) e implementa os desafios extras
(indicadores, exportação CSV, login e notificação simulada de e-mail), integrando
os principais conteúdos da disciplina em uma aplicação desktop funcional.
