#!/bin/bash
# Compila e executa o Primeiro Teste JDBC com SQLite.  Uso: bash run.sh
set -e
cd "$(dirname "$0")"

CP="lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-nop.jar"   # no Windows use ";" no lugar de ":"

echo ">> Compilando..."
javac -cp "$CP" -d out src/Main.java

echo ">> Executando..."
java -cp "out:$CP" Main
