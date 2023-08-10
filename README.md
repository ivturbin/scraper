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
