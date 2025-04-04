## Веб-скрейпер
Предназначен для загрузки данных с веб-сервиса.  
Данные загружаются в локальную БД на Postgres в виде таблицы событий с файлами и информацией по судебным делам.  

```
ВНИМАНИЕ: ввиду ограниченности пропускной способности сервера крайне не рекомендуется запускать приложение
одновременно в нескольких экземплярах на разных портах/серверах, формируя запросы с одного IP-адреса.
На текущий момент вычисленная пропускная способность сервера по выдаче данных составляет порядка 960 судебных дел 
в сутки для одного IP-адреса.  
Попытка увеличить данный показатель может привести к бану.
```

### Локальный запуск в среде разработки

1. Клонировать и собрать проект
2. Запустить docker compose - поднимется локальная БД в докер контейнере
3. Запустить приложение с профилем `local` и `configuration.scheduled_scraping_enabled=true`

### Запуск в Linux
#### 1. Требования к установке и подготовка
- docker (в Linux приложение тестировалось только с конфигурацией chrome-docker, см. Конфигурацию приложения)
- OpenJDK-17 совместимый JRE для запуска
- Наличие переменных среды: JAVA_HOME и PATH
- cron для перезапуска приложения по расписанию

Проверить наличие джавы и соответствие версии:
```
java -version
```

#### 2. Запуск в сеансе пользователя
В рабочей директории должны быть:
- application.properties
- scraper-{Версия}.jar

Из рабочей директории:
```
sudo java -jar "scraper-{Версия}.jar" &> "$(date +"%Y-%m-%d_%H:%M:%S")_scraper.log" 2>&1 &
```
**Ctrl+C** чтобы остановить выполнение jar.

Либо detached:
```
nohup sudo java -jar "scraper-{Версия}.jar" &> "$(date +"%Y-%m-%d_%H:%M:%S")_scraper.log" 2>&1 &
```
##### 2.1 Запуск скриптом

Файл restart.sh является скриптом перезапуска приложения (приложение будет запущено, если и не запускалось до этого).  
В рабочей директории должны быть:
- application.properties
- scraper-{Версия}.jar
- restart.sh

Перед запуском обязательно:
```
chmod +x restart.sh
```
Запуск:
```
sudo ./restart.sh
```

_В restart.sh необходимо вписать путь до рабочей директории включая саму директорию и версию скрейпера._

#### 3. Создание задачи cron
Были обнаружены утечки памяти при использовании браузера Chrome и фреймворка Selenium. Самое простое решение - перезапуск приложения по расписанию, для чего отлично подходит cron.
После шага 2.1 становится возможным создать задачу cron:
```
sudo crontab -e
```
_Примечание: команда запускается от суперпользователя, потому что в текущей реализации при использовании конфигурации chrome-docker приложению требуются 
права суперпользователя для работы с контейнерами docker._

Далее открывается файл задач cron, куда необходимо добавить задачу:
```
0 0 * * * /bin/bash {Рабочая директория}/restart.sh >> {Рабочая директория}/cron.log
```
_В приведенном примере приложение будет перезапускаться каждый день в полночь с дописыванием bash stdout в файл cron.log._

### Запуск в Windows
#### 1. Требования к установке и подготовка
- В системе должен быть установлен Google Chrome или Docker Desktop (для chrome-docker, см. Конфигурацию приложения)
- При сборке приложения использовался OpenJDK-17. Требуется такой же или совместимый JRE для запуска
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

Файл start.ps1 является скриптом Powershell и содержит в себе команду запуска приложения.  
Для использования необходимо, чтобы в одном каталоге лежали файлы:
- application.properties
- scraper-1.0.0.jar
- start.ps1

#### 3. Установка и запуск в качестве службы Windows
Для установки приложения в качестве службы Windows использовать утилиту nssm  
Можно скачать по адресу: https://www.nssm.cc/download

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

### Конфигурация приложения
Приложение запускается с двумя профилями - `local`(application-local.properties) и `default`(application.properties).
Первый очень удобно использовать вместе с локальной поднятой в докере БД.

application.properties - заготовка файла конфигураций, который нужно класть в рабочую директорию рядом с джарником.

###### Назначение конфигураций файла application.properties:
```
server.port - порт, на котором запускается приложение
spring.main.banner-mode = off - выключить вывод баннера спринг в консоль

configuration.main_url = {} - url сервиса
configuration.browser={chrome-docker, chrome, mozilla} - используемый браузер

spring.datasource.url = {} - url БД Postgresql
spring.datasource.driverClassName = org.postgresql.Driver - класс драйвера БД
spring.datasource.username = {} - имя пользователя БД  
spring.datasource.password = {} - пароль пользователя БД  
springdoc.swagger-ui.tryItOutEnabled = true - убрать кнопки Try it out в Swagger UI  
spring.main.lazy-initialization = false - ленивая инициализация бинов для быстрого запуска, нет необходимости

configuration.selenium_awaiting_timeout = {} - таймаут, в течение которого селениум продолжает опрашивать драйвер на наличие веб элемента  
configuration.scraping_interval = {} - интервал скрепинга, мс (рекомендуется не менее 80000)  
configuration.additional_awaiting_on_error = {} - доп. ожидание на ошибке, мс (рекомендуется не менее 20000)  
configuration.scheduled_scraping_enabled = {} - включение интервального скрейпинга при запуске приложения (true) 

management.endpoints.web.exposure.include = info, env, metrics, scheduledtasks, health, conditions, configprops - конфигурация эндпоинтов Spring Actuator
management.info.env.enabled = true - включить эндпоинт /actuator/info
```

### API
###### Приложение предоставляет следующие http-эндпоинты:

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
###### Swagger-UI 
доступен по стандартному адресу
http://localhost:{server.port}/swagger-ui/index.html
