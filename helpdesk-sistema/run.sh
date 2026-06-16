#!/bin/bash
# Compila e executa o Sistema de Gestão de Chamados (Help Desk).
# Uso:  bash run.sh
set -e
cd "$(dirname "$0")"

# Classpath com o driver SQLite e as dependências de log.
# No Windows, troque os ":" por ";" (ou use o arquivo run.bat).
CP="lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-nop.jar"

echo ">> Compilando o projeto..."
# Compila todos os .java do projeto para a pasta out/
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "$CP" -d out @sources.txt
rm -f sources.txt

echo ">> Iniciando a aplicação..."
java -cp "out:$CP" helpdesk.Main
