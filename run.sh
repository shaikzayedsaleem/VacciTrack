#!/bin/bash
# ── VaxDrive Run Script ──────────────────────────────────────
JAR_PATH="lib/mysql-connector-j-8.0.33.jar"

if [ ! -d "out" ]; then
    echo "⚠️  Not compiled yet. Running compile.sh first..."
    bash compile.sh
fi

echo "🚀 Starting VaxDrive..."
java -cp "out:$JAR_PATH" com.vaccination.Main
