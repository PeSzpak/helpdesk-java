# Helpdesk Java

Dois projetos Java focados em persistência com banco de dados SQLite, desenvolvidos durante a graduação em Análise e Desenvolvimento de Sistemas.

## Projetos

### [`helpdesk-system/`](./helpdesk-system)
Sistema de helpdesk desktop completo com interface gráfica Swing e arquitetura em camadas.

**Tecnologias:** Java · Swing (GUI) · SQLite · JDBC · DAO Pattern

**Funcionalidades:**
- Tela de login com validação de usuário
- Cadastro e acompanhamento de chamados
- Exportação de relatórios em CSV
- Notificação por e-mail
- Persistência local com SQLite

**Estrutura:**
```
src/helpdesk/
├── dao/    → ChamadoDAO, UsuarioDAO, ConexaoSQLite
├── model/  → Chamado, Usuario
├── util/   → EmailNotificacao, ExportadorCSV, Validador
└── view/   → LoginFrame, PrincipalFrame, UITheme
```

**Como rodar:**
```bash
cd helpdesk-system
./run.sh        # Linux / macOS
run.bat         # Windows
```

---

### [`jdbc-sqlite-example/`](./jdbc-sqlite-example)
Primeiro contato com JDBC: CRUD completo de produtos usando SQLite sem frameworks.

**Tecnologias:** Java · SQLite · JDBC

**Como rodar:**
```bash
cd jdbc-sqlite-example
./run.sh
```

## Pré-requisitos

- Java 11+
- Dependências já incluídas em `lib/` (sqlite-jdbc, slf4j)

## Licença

MIT © [Pedro Szpak](https://github.com/PeSzpak)
