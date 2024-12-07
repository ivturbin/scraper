# Используем официальный образ PostgreSQL
FROM postgres:15

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /docker-entrypoint-initdb.d

# Копируем скрипты SQL в контейнер
COPY ./src/main/resources/migration/ ./

# Образы PostgreSQL автоматически выполняют SQL-скрипты в /docker-entrypoint-initdb.d при старте
# Никаких дополнительных инструкций не требуется

# Порт по умолчанию для PostgreSQL
EXPOSE 5432
