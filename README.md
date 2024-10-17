# ambient-svet
# Dynamic ambient light for pc monitor

# Ambient свет программа для работы с контроллером arduino динамической фоновой подсветки

### Команды для linux
### Display Detected System’s Serial Support Under Linux
```
dmesg | grep tty
```

### A note about USB based serial ports
```
dmesg | grep -i serial dmesg | grep -i FTDI
```

### запуск конфигуратора:
```
java -jar configurator-0.0.1-jar-with-dependencies.jar
```

### запуск фоновой подсветки:
```
java -jar capture-screen-0.0.1-jar-with-dependencies.jar
```