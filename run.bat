@echo off
REM ── VaxDrive Run (Windows) ──────────────────────────────────
set JAR=lib\mysql-connector-j-8.0.33.jar

if not exist out (
    echo Not compiled yet. Running compile.bat first...
    call compile.bat
)

echo Starting VaxDrive...
java -cp "out;%JAR%" com.vaccination.Main
