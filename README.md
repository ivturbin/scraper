## Веб-скрейпер
Предназначен для загрузки данных с веб-сервиса.  
Данные загружаются в локальную БД на Postgres в виде таблицы событий с файлами и информацией по судебным делам.  

```
ВНИМАНИЕ: ввиду ограниченности пропускной способности сервера, крайне не рекомендуется запускать приложение
одновременно в нескольких экземплярах на разных портах/серверах, формируя запросы с одного IP-адреса.
На текущий момент вычисленная пропускная способность сервера по выдаче данных составляет порядка 960 судебных дел 
в сутки для одного IP-адреса.  
Попытка увеличить данный показатель может привести к бану.
```

#### 1. Требования к установке и подготовка
- В системе должен быть установлен Google Chrome.
- При сборке приложения использовался OpenJDK-18. Требуется такой же или аналогичный
- Убедиться в наличии переменных среды: JAVA_HOME и PATH
- Здесь и далее все команды в Windows PowerShell

Проверить наличие джавы и соответствие версии:
```
java -version
```

#### 2. Запуск в сеансе пользователя (для тестирования приложения)
Шрифт, установка кодировки в консоли:
Properties -> Font -> Lucida Console -> OK
```
chcp 65001
```
Положить заполненный application.properties рядом с джарником

```
java -jar scraper-{VERSION}.jar
```

Ctrl+C чтобы остановить выполнение jar

Файл resources/start.ps1 является скриптом powershell и содержит в себе команду запуска приложения.  
Для использования необходимо, чтобы в одном каталоге лежали файлы:
- application.properties
- scraper-1.0.0.jar
- start.ps1

Назначение конфигураций файла application.properties:
```

server.port - номер порта приложения

configuration.selenium_awaiting_timeout - таймаут, в течение которого селениум продолжает опрашивать драйвер на наличие веб элемента  
configuration.scraping_interval - интервал скрепинга, мс (рекомендуется не менее 80000)  
configuration.additional_awaiting_on_error - доп. ожидание на ошибке, мс (рекомендуется не менее 20000)  
configuration.scheduled_scraping_enabled - включение интервального скрейпинга при запуске приложения (true) 

spring.main.banner-mode=off - выключить вывод баннера спринг в консоль

configuration.main_url - url сервиса

spring.datasource.url- url БД Postgresql (напр. jdbc:postgresql://msk2c:5432/KAD_db)  
spring.datasource.driverClassName - класс драйвера (org.postgresql.Driver)  
spring.datasource.username - имя пользователя БД  
spring.datasource.password - пароль пользователя БД  
springdoc.swagger-ui.tryItOutEnabled = true - убрать кнопки Try it out в Swagger UI  
spring.main.lazy-initialization=true - ленивая инициализация бинов для быстрого запуска  
```

#### 3. Установка и запуск в качестве службы Windows
Для установки приложения в качестве службы Windows использовать утилиту nssm  
Можно скачать по адресу: https://www.nssm.cc/download  
Также утилита выложена в X:\7.ОИТ\iturbin

Установка службы выполняется пользователем с административными правами через команду консоли:
```
nssm.exe install [Имя службы]
```
в окне настроек службы утилиты заполнить:  
- на вкладке Application:  
  - Path: java (либо полный путь к исполняемому файлу Java)  
  - Startup directory: каталог приложения
  - Arguments: -jar scraper-{VERSION}.jar
- На вкладке I/O:
  - Output (stdout): файл вывода, например "output" в каталоге приложения
  - Error (stderr): файл вывода ошибок, например "err" в каталоге приложения



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
curl --location 'http://{host}:{port}/scrape?caseNumber={Номер дела}'
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
##### 5. Swagger-UI 
доступен по стандартному адресу
http://localhost:{server.port}/swagger-ui/index.html
