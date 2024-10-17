/*
  Управление RGB-лентой на WS2812 v.: 1.1
   - управление цветами светодиодной ленты по "протоколу" Ada
   - сохранение цвета, отображаемого при старте, в eeprom
   - включение/отключение отображения при запуске контроллера
*/

//----------------------НАСТРОЙКИ-----------------------
#define NUM_LEDS 94          // число светодиодов в ленте
#define DI_PIN 13            // пин, к которому подключена лента
#define START_FLASHES 0      // проверка цветов при запуске (1 - включить, 0 - выключить)
#define serialRate 115200    // скорость связи с ПК

//#define DEBUG_MODE           // режим отладки

//----------------------КОМАНДЫ-----------------------
#define SHOW_ARRAY_COLOR_CMD 0 // [A] [d] [a] [hi] [lo] [chk] [ | SHOW_ARRAY_COLOR_CMD] {[r] [g] [b]} ... NUM_LEDS
#define SHOW_SOLID_COLOR_CMD 1 // [c] [m] [d] [hi] [lo] [chk] [SHOW_SOLID_COLOR_CMD] [r] [g] [b] [save/not save: 1|0]
#define SET_STARTUP_MODE_CMD 2 // [c] [m] [d] [hi] [lo] [chk] [SET_STARTUP_MODE_CMD] [on/off: 1|0]

//----------------------МАКРОСЫ ДЛЯ РАБОТЫ С EEPROM-----------------------
#define GET_R EEPROM.read(0)                        // Red
#define GET_G EEPROM.read(1)                        // Green
#define GET_B EEPROM.read(2)                        // Blue
#define GET_START_UP_MODE EEPROM.read(3)            // Mode
#define SET_R(x) EEPROM.write(0, (x))               // Red
#define SET_G(x) EEPROM.write(1, (x))               // Green
#define SET_B(x) EEPROM.write(2, (x))               // Blue
#define SET_START_UP_MODE(x) EEPROM.write(3, (x))   // Mode

#include <EEPROM.h>
#include <FastLED.h>
CRGB leds[NUM_LEDS];
uint8_t adaPrefix[] = {'A', 'd', 'a'};
uint8_t cmdPrefix[] = {'c', 'm', 'd'};
uint8_t cmdSize = 3;
uint8_t hi, lo, chk, i;

void setup()
{
  FastLED.addLeds<WS2812, DI_PIN, GRB>(leds, NUM_LEDS);

  // вспышки красным синим и зелёным при запуске
  if (START_FLASHES) {
    LEDS.showColor(CRGB(255, 0, 0));
    delay(500);
    LEDS.showColor(CRGB(0, 255, 0));
    delay(500);
    LEDS.showColor(CRGB(0, 0, 255));
    delay(500);
    LEDS.showColor(CRGB(0, 0, 0));
  }

  if(GET_START_UP_MODE == 1) {
    LEDS.showColor(CRGB(GET_R, GET_G, GET_B));
  } else {
    LEDS.showColor(CRGB(0, 0, 0));
  }

  Serial.begin(serialRate);
  Serial.print("Ada\n");
}

void loop() {
  bool scan;
  bool isAdaPrefix;
  bool isCmdPrefix;

  do {
    scan = false;
    isAdaPrefix = true;
    isCmdPrefix = true;

    for (i = 0; i < cmdSize; ++i) {
      byte bt = readByte();

      if(adaPrefix[i] != bt) {
        isAdaPrefix = false;
      }

      if(cmdPrefix[i] != bt) {
        isCmdPrefix = false;
      }

      if (!isAdaPrefix && !isCmdPrefix) {
        scan = true;
        break;
      }
    }
  } while (scan);

  hi = readByte();
  lo = readByte();
  chk = readByte();

  if (chk != (hi ^ lo ^ 0x55)) {
#ifdef DEBUG_MODE
    Serial.print("Wrong CRC");
#endif
    return;
  }

  if(isAdaPrefix) {
    showArrayColor();
  } else if(isCmdPrefix) {
    executeCommand(readByte());
  }
}

void showArrayColor() {
  memset(leds, 0, NUM_LEDS * sizeof(struct CRGB));
  for (uint8_t i = 0; i < NUM_LEDS; i++) {
    leds[i].r = readByte();
    leds[i].g = readByte();
    leds[i].b = readByte();
  }
  FastLED.show();  // записываем цвета в ленту
}

void showSolidColor() {
  byte r, g, b, save;
  r = readByte();
  g = readByte();
  b = readByte();
  save = readByte();

  if(save == 1) {
    saveSolidColor(r, g, b);
  }

  LEDS.showColor(CRGB(r, g, b));
}

void saveStartupMode() {
  SET_START_UP_MODE(readByte());
}

void executeCommand(byte cmd) {
  switch (cmd) {
    case SHOW_ARRAY_COLOR_CMD:
      showArrayColor();
    break;
    case SHOW_SOLID_COLOR_CMD:
      showSolidColor();
    break;
    case SET_STARTUP_MODE_CMD:
      saveStartupMode();
    break;
#ifdef DEBUG_MODE
    default:
      Serial.print("Undefined command");
#endif
  }
}

byte readByte() {
  while (!Serial.available());
  return Serial.read();
}

void saveSolidColor(byte r, byte g, byte b) {
  SET_R(r);
  SET_G(g);
  SET_B(b);
}
