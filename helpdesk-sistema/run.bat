@echo off
REM Compila e executa o Sistema de Gestao de Chamados (Help Desk) no Windows.
REM Uso: dê dois cliques ou execute "run.bat" no Prompt de Comando.
cd /d "%~dp0"

set CP=lib\sqlite-jdbc.jar;lib\slf4j-api.jar;lib\slf4j-nop.jar

echo >> Compilando o projeto...
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "%CP%" -d out @sources.txt
del sources.txt

echo >> Iniciando a aplicacao...
java -cp "out;%CP%" helpdesk.Main
pause
