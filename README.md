## Веб-скрейпер

#### 1. Подготовка
При сборке использовался OpenJDK-18. Требуется такой же или аналогичный\
Убедиться в наличии переменных среды: JAVA_HOME и PATH\
Далее все команды в Windows PowerShell

Проверить наличие джавы и соответствие версии:
```
java -version
```

#### 2. Нормальный вывод логов Windows PowerShell
Properties -> Font -> Lucida Console -> OK
```
chcp 65001
```

#### 3. Запуск
Положить заполненный application.properties рядом с джарником

```
java -jar scraper-{VERSION}.jar
```

Ctrl+C чтобы остановить выполнение jar

Файл resources/start.ps1 является скриптом powershell и содержит в себе команду запуска приложения.
Для использования необходимо, чтобы в одной папке лежали файлы:
application.properties
scraper-1.0.0.jar
start.ps1

#### 4. API
###### Приложение предоставляет следующие http-методы:

Запуск интервального скрейпинга.
Шедулер в указанный в application.properties интервал будет запускать скрейпинг
```
curl --location --request POST 'http://{host}:{port}/start'
```
Остановка интервального скрейпинга.
```
curl --location --request POST 'http://{host}:{port}/stop'
```
Скрейпинг дела по номеру.
```
curl --location 'http://{host}:{port}/scrape' 
--header 'Content-Type: application/json' 
--data '{номер дела}'
```
Скрейпинг всех дел из таблицы court_case. 
```
curl --location --request POST 'http://{host}:{port}/scrape/all'
```
Скрейпинг следующего дела.
```
curl --location --request POST 'http://{host}:{port}/scrape/next'
```
Проверка состояния приложения.
```
curl --location 'http://localhost:9001/status'
```