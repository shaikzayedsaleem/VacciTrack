#!/bin/bash
# ── VaxDrive Compile Script ──────────────────────────────────
set -e

JAR_PATH="lib/mysql-connector-j-8.0.33.jar"

if [ ! -f "$JAR_PATH" ]; then
    echo "❌  ERROR: $JAR_PATH not found."
    echo "   Download from https://dev.mysql.com/downloads/connector/j/"
    echo "   and place the .jar in the lib/ folder."
    exit 1
fi

echo "📦 Compiling VaxDrive..."
find src -name "*.java" > sources.txt

mkdir -p out

javac -cp "$JAR_PATH" \
      -d out \
      -encoding UTF-8 \
      @sources.txt

echo "✅ Compilation successful! Run with:  bash run.sh"
