# Primeiro Teste JDBC com SQLite

Atividade prática: conectar uma aplicação Java a um banco SQLite via JDBC e
exibir `Conectado!`.

## Estrutura

```
PrimeiroTesteJDBC/
├── src/Main.java        # código da aplicação
├── lib/
│   ├── sqlite-jdbc.jar  # driver JDBC do SQLite (org.xerial 3.46.1.3)
│   ├── slf4j-api.jar    # dependência de log do driver
│   └── slf4j-nop.jar    # silencia os avisos de log (saída limpa)
├── run.sh               # compila e executa
├── RELATORIO.md         # relatório da atividade
└── produtos.db          # criado automaticamente na 1ª execução
```

## Como executar (macOS/Linux)

```bash
bash run.sh
```

Ou manualmente:

```bash
javac -cp "lib/sqlite-jdbc.jar:lib/slf4j-api.jar" -d out src/Main.java
java  -cp "out:lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-nop.jar" Main
```

> No Windows, troque o separador de classpath `:` por `;`.

## Saída esperada

```
Conectado!
```

## Erro comum
`No suitable driver found for jdbc:sqlite:...` → o `sqlite-jdbc.jar` não está no
classpath; confira o `-cp`.
