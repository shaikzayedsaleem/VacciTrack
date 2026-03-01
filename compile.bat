@echo off
REM ── VaxDrive Compile (Windows) ──────────────────────────────
set JAR=lib\mysql-connector-j-8.0.33.jar

if not exist %JAR% (
    echo ERROR: %JAR% not found.
    echo Download from https://dev.mysql.com/downloads/connector/j/
    pause & exit /b 1
)

echo Compiling VaxDrive...
if not exist out mkdir out

dir /s /b src\*.java > sources.txt
javac -cp %JAR% -d out -encoding UTF-8 @sources.txt

if %ERRORLEVEL% == 0 (
    echo Compilation successful! Run with: run.bat
) else (
    echo Compilation FAILED.
)
pause
