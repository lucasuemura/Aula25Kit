@echo off
chcp 65001 > nul
cls
echo ======================================================================
echo   SafePay FinTech • Compilando e Executando o Laboratorio (Aula 25)
echo   Sistema Operacional: Windows 10 / 11
echo ======================================================================
echo.

REM 1. Verifica se o Java Development Kit (JDK) esta instalado
where javac >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERRO] O compilador javac nao foi encontrado no PATH do Windows!
    echo Certifique-se de ter o JDK instalado.
    pause
    exit /b 1
)

REM 2. Cria a pasta bin caso nao exista
if not exist "bin" mkdir bin

echo [1/2] Compilando as classes Java com a biblioteca SQLite...
javac -encoding UTF-8 -cp "lib/*" -d bin src/br/com/safepay/model/*.java src/br/com/safepay/factory/*.java src/br/com/safepay/db/*.java src/br/com/safepay/seguranca/*.java src/br/com/safepay/main/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Ocorreu uma falha durante a compilacao do codigo.
    pause
    exit /b 1
)

echo [2/2] Compilacao concluida com sucesso!
echo.
echo Iniciando a aplicacao interativa...
echo ======================================================================
java -cp "bin;lib/*" br.com.safepay.main.MainApp

echo.
echo ======================================================================
echo Execucao finalizada.
pause
