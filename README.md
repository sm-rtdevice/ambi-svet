# ambient-svet
# Dynamic ambient light for pc monitor

# Ambient свет программа для работы с контроллером динамической фоновой подсветки

### Библиотека jSSC (Java Simple Serial Connector) для работы с COM-портом, пример работы:
https://micro-pi.ru/jssc-%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%B0%D0%B5%D0%BC-com-%D0%BF%D0%BE%D1%80%D1%82%D0%BE%D0%BC-java-raspberry/

### Репозиторий библиотеки
https://mvnrepository.com/artifact/io.github.java-native/jssc/2.9.4

### Библиотека FastLED для работы с адресной светодиодной лентой
https://github.com/FastLED/FastLED

### Команды для linux
### Display Detected System’s Serial Support Under Linux
dmesg | grep tty

### A note about USB based serial ports
dmesg | grep -i serial dmesg | grep -i FTDI

### запуск конфигуратора:
```
java -jar configurator-0.0.1-jar-with-dependencies.jar
```

### запуск фоновой подсветки:
```
java -jar capture-screen-0.0.1-jar-with-dependencies.jar
```

ls -la /dev/tty*
stty -F /dev/tty -a