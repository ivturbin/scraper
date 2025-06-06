#!/bin/bash

HOME="{Рабочая директория}"

JAR_PATH="$HOME/scraper-{Версия}.jar"
LOG_PATH="$HOME/log/$(date +"%Y-%m-%d_%H:%M:%S").log"
PID_FILE="$HOME/scraper.pid"

echo ""

# Если PID-файл существует, завершить процесс
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
        echo "Stopping existing process with PID $PID"
        kill $PID
        sleep 30
    fi
    rm -f "$PID_FILE"
fi
# Запустить приложение и сохранить его PID
echo "$(date +"%Y-%m-%d_%H:%M:%S") Starting new process:"
nohup sudo java -jar "$JAR_PATH" "--spring.config.location=$HOME/application.properties" &> "$LOG_PATH" 2>&1 &
echo $! > "$PID_FILE"
echo "$(<$PID_FILE)"