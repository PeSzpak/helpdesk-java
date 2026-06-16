# Relatório — Primeiro Teste JDBC com SQLite

**Aluno:** _(seu nome)_
**Disciplina:** _(nome da disciplina)_
**Data:** 30/05/2026

---

## 1. O que é JDBC

JDBC (*Java Database Connectivity*) é a API padrão do Java para acessar bancos de
dados relacionais. Ela define interfaces como `Connection`, `Statement` e
`ResultSet` que permitem ao programa enviar comandos SQL e receber resultados,
**independentemente** do banco utilizado. Para cada banco existe um **driver
JDBC** específico, que traduz as chamadas Java para o protocolo daquele banco.
Assim, o mesmo código funciona com SQLite, MySQL, PostgreSQL etc., bastando
trocar o driver e a URL de conexão.

## 2. O que é SQLite

SQLite é um banco de dados relacional **leve e embutido**. Diferente de bancos
cliente-servidor (como o MySQL), ele não exige um servidor rodando: todo o banco
fica em um **único arquivo** no disco (aqui, `produtos.db`). É ideal para
aprendizado, protótipos e aplicativos pequenos, pois não precisa de instalação
nem configuração de servidor. Se o arquivo não existir, ele é criado
automaticamente na primeira conexão.

## 3. Como funciona a URL JDBC utilizada

A linha central do programa é:

```java
DriverManager.getConnection("jdbc:sqlite:produtos.db");
```

A URL `jdbc:sqlite:produtos.db` tem três partes:

| Parte         | Significado                                                         |
|---------------|--------------------------------------------------------------------|
| `jdbc`        | Protocolo — indica que é uma conexão via JDBC.                      |
| `sqlite`      | Subprotocolo — identifica o tipo do banco (qual driver será usado).|
| `produtos.db` | Caminho/nome do arquivo do banco (criado se ainda não existir).     |

O `DriverManager` lê essa URL, localiza o driver SQLite (`sqlite-jdbc.jar`) no
*classpath* e o usa para abrir a conexão, retornando um objeto `Connection`.

**Fluxo completo:** Aplicação Java → Driver JDBC → Banco SQLite → Conexão Estabelecida.

## 4. Finalidade do bloco try/catch

O `try/catch` serve para **tratar erros** que podem ocorrer ao conectar. A
abertura da conexão pode lançar uma `SQLException` (por exemplo, se o driver não
estiver no classpath ou o caminho for inválido). No `try` fica o código que pode
falhar; se a conexão der certo, é exibido `Conectado!`. Se ocorrer a exceção, o
fluxo desvia para o `catch`, que mostra `e.getMessage()` em vez de quebrar o
programa abruptamente. O programa usa ainda o *try-with-resources*, que **fecha a
conexão automaticamente** ao final, evitando vazamento de recursos.

## 5. Resultado obtido

Ao executar o programa, o console exibiu:

```
Conectado!
```

confirmando que a comunicação entre a aplicação Java e o banco SQLite foi
estabelecida com sucesso. O arquivo `produtos.db` foi criado automaticamente na
pasta do projeto. _(Anexar print da tela.)_
