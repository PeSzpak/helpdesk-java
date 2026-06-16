# 🎧 Sistema de Gestão de Chamados — Help Desk

Projeto Final Integrador — **Análise e Projeto de Sistemas** (Faculdade Senac PR).
Aplicação **desktop em Java Swing** com persistência em **SQLite via JDBC**.

---

## ▶️ Como executar

> Pré-requisito: **JDK 17+** instalado (`java -version`). O driver SQLite já está incluído na pasta `lib/`.

**macOS / Linux:**
```bash
bash run.sh
```

**Windows:**
```bat
run.bat
```

O script compila tudo para a pasta `out/` e inicia a aplicação. Na primeira execução o
arquivo de banco `helpdesk.db` é criado automaticamente.

**Login padrão:** usuário `admin` — senha `1234`

---

## 🗂️ Estrutura do projeto

```
Atividade-Chamada/
├── lib/                       → driver SQLite (sqlite-jdbc) + libs de log
├── src/helpdesk/
│   ├── Main.java              → ponto de entrada (inicia banco + login)
│   ├── model/                 → classes de dados (POO)
│   │   ├── Chamado.java
│   │   └── Usuario.java
│   ├── dao/                   → acesso a dados (JDBC)
│   │   ├── ConexaoSQLite.java → conexão + criação das tabelas
│   │   ├── ChamadoDAO.java    → CRUD + relatórios
│   │   └── UsuarioDAO.java    → autenticação (login)
│   ├── util/                  → utilitários
│   │   ├── Validador.java     → validações e regras de negócio
│   │   ├── ExportadorCSV.java → exportação de arquivos
│   │   └── EmailNotificacao.java → notificação simulada
│   └── view/                  → interface gráfica (Swing)
│       ├── UITheme.java       → cores e componentes estilizados
│       ├── LoginFrame.java    → tela de login
│       └── PrincipalFrame.java→ tela principal (CRUD + abas)
├── run.sh / run.bat           → scripts de compilação/execução
└── docs/                      → espaço para prints das telas
```

A arquitetura segue o fluxo pedido no enunciado:
**Usuário → Interface Swing → Lógica/Validações → JDBC → Banco SQLite**

---

## 🧩 Como cada requisito foi atendido

### Funcionalidades obrigatórias
| Requisito | Onde está | Como foi feito |
|-----------|-----------|----------------|
| **Cadastro de chamados** | `PrincipalFrame.salvar()` + `ChamadoDAO.inserir()` | Formulário valida os dados e insere via `INSERT`. |
| **Consulta com filtros** | `ChamadoDAO.listarComFiltro()` | `SELECT` dinâmico por texto, status, prioridade e setor. |
| **Atualização** | `PrincipalFrame.alterar()` + `ChamadoDAO.atualizar()` | `UPDATE` do registro selecionado na tabela. |
| **Exclusão com confirmação** | `PrincipalFrame.excluir()` | `JOptionPane.showConfirmDialog` antes do `DELETE`. |
| **Relatórios básicos** | aba *Relatórios* + `contarPorStatus()/contarPorPrioridade()` | `GROUP BY` mostrado em cartões e relatório textual. |
| **Buscar / Filtrar** | barra de busca na aba *Chamados* | Busca por número, solicitante ou descrição + filtro de status. |

### Banco de dados (SQLite + JDBC)
- Tabela `chamados` criada exatamente como o enunciado em `ConexaoSQLite.java`.
- Conexão JDBC com `DriverManager` e `Connection`.
- Todas as operações usam **`PreparedStatement`** (seguro contra SQL Injection) e leitura com **`ResultSet`**.

### Regras de negócio
Implementadas em `Validador.java` e `PrincipalFrame`:
- ❌ Campos obrigatórios não podem ficar vazios.
- 🔑 Número do chamado é **único** (`numeroJaExiste()` + `UNIQUE` na tabela).
- 🎚️ Prioridade só aceita **Baixa, Média ou Alta**.
- 🚦 Status só aceita **Aberto, Em Atendimento ou Finalizado**.
- 📅 Data de abertura é **preenchida automaticamente** ao salvar.
- 🔒 Chamado **finalizado não pode ser excluído** (apenas alteração de status).
- ✅ Toda exclusão pede confirmação via `JOptionPane`.

### Recursos obrigatórios
- **POO**: classes `Chamado`, `Usuario` e separação em camadas.
- **Organização em métodos** e pacotes (`model`, `dao`, `util`, `view`).
- **Eventos**: `ActionListener` nos botões e `ListSelectionListener` na tabela.
- **Coleções**: `ArrayList`/`List` e `Map` para manipular os dados.
- **Tratamento de exceções**: `try/catch/finally` (via try-with-resources) e exceção própria `ValidacaoException`.
- **JDBC**: `Connection`, `PreparedStatement`, `ResultSet`.
- **Interface Swing**: `JFrame`, `JPanel`, `JTabbedPane`, `JTable`, `JComboBox`, `JOptionPane`, etc.
- **Persistência SQLite**: arquivo `helpdesk.db`.

### Desafios extras (bônus) ⭐
- **Indicadores**: total, abertos, em atendimento e finalizados (aba *Relatórios*).
- **Relatório** por status e prioridade com barras proporcionais.
- **Exportação CSV** (`ExportadorCSV.java` — manipulação de arquivos).
- **Login** com usuário/senha em tabela SQLite (`LoginFrame` + `UsuarioDAO`).
- **E-mail simulado** ao finalizar um chamado (`EmailNotificacao` — saída no console).

---

## 🖥️ Telas
1. **Login** — autenticação do usuário.
2. **Chamados** — formulário de cadastro/edição à esquerda, tabela com busca à direita.
3. **Relatórios** — cartões de indicadores, relatório textual e exportação CSV.
4. **Sobre** — informações do sistema.

> Coloque os prints das telas em funcionamento na pasta `docs/` para anexar à entrega.

---

## 💡 Dicas de uso
- Clique em **Novo** para limpar o formulário (o número já vem sugerido).
- Clique em uma linha da tabela para **editar**; depois use **Alterar**.
- Marque o status como **Finalizado** e clique em **Alterar** para ver a notificação simulada no console.
- Use a aba **Relatórios → Exportar CSV** para gerar o arquivo de chamados.
