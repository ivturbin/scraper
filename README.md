## Web Scraper
Designed for downloading data from a web service.
The data is loaded into a local PostgreSQL database in the form of event tables with files and information on court cases.

```
WARNING: Due to the server's limited bandwidth, it is highly discouraged to run the application
simultaneously in multiple instances on different ports/servers, forming requests from a single IP address.
Currently, the estimated server bandwidth for data output is about 960 court cases per day per IP address.
Attempting to increase this rate may result in a ban.
```

#### 1. Installation Requirements and Preparation
- Google Chrome must be installed on the system.
- The application was built using OpenJDK-18. The same or a similar version is required.
- Ensure the presence of environment variables: JAVA_HOME and PATH.
- All commands below are for Windows PowerShell.

Check Java installation and version:
```
java -version
```

#### 2. Running in User Session (for testing the application)
Font and encoding settings in the console:
Properties -> Font -> Lucida Console -> OK
```
chcp 65001
```
Place the filled application.properties file next to the jar file.

```
java -jar scraper-{VERSION}.jar
```
Press Ctrl+C to stop the jar execution.

The file resources/start.ps1 is a PowerShell script and contains the command to run the application.
To use it, the following files must be in the same directory:
- application.properties
- scraper-1.0.0.jar
- start.ps1

Configuration settings in the application.properties file:
```
server.port - application port number

spring.datasource.url - PostgreSQL database URL (e.g., jdbc:postgresql://msk2c:5432/KAD_db)  
spring.datasource.driverClassName - driver class (org.postgresql.Driver)  
spring.datasource.username - database username  
spring.datasource.password - database user password  
springdoc.swagger-ui.tryItOutEnabled = true - remove Try it out buttons in Swagger UI
spring.main.lazy-initialization=true - lazy initialization of beans for quick startup
spring.main.banner-mode=off - disable Spring banner output in console

configuration.main_url - service URL
configuration.browser - which browser to use
configuration.selenium_awaiting_timeout - timeout during which Selenium continues to poll the driver for the presence of a web element  
configuration.scraping_interval - scraping interval in ms (recommended not less than 80000)  
configuration.additional_awaiting_on_error - additional wait time on error in ms (recommended not less than 20000)  
configuration.scheduled_scraping_enabled - enable interval scraping at application startup (true) 
```

#### 3. Installation and Running as a Windows Service
To install the application as a Windows service, use the nssm utility.
You can download it from: https://www.nssm.cc/download
The utility is also available at X:\7.ОИТ\iturbin

Service installation is performed by a user with administrative rights through the console command:
```
nssm.exe install [Service Name]
```
In the service settings window of the utility, fill out:

- On the Application tab:
-- Path: java (or the full path to the Java executable)
-- Startup directory: application directory
-- Arguments: -jar scraper-{VERSION}.jar
- On the I/O tab:
-- Output (stdout): output file, e.g., "output" in the application directory
-- Error (stderr): error output file, e.g., "err" in the application directory



#### 4. API
###### The application provides the following HTTP methods:

Start interval scraping.
The scheduler will start scraping at the interval specified in application.properties.
```
curl --location --request POST 'http://{host}:{port}/start'
```
Stop interval scraping.
```
curl --location --request POST 'http://{host}:{port}/stop'
```
Scrape a case by number.
```
curl --location 'http://{host}:{port}/scrape?caseNumber={Номер дела}'
```
Scrape all cases from the court_case table.
```
curl --location --request POST 'http://{host}:{port}/scrape/all'
```
Scrape the next case.
```
curl --location --request POST 'http://{host}:{port}/scrape/next'
```
Check the application status.
```
curl --location 'http://localhost:9001/status'
```
##### 5. Swagger-UI 
available at the standard address
http://localhost:{server.port}/swagger-ui/index.html
