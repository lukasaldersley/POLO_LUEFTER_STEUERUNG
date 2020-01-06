#include <EEPROM.h>
#define RELAIS 2
#define ADDRESS 0
#define OFF LOW
#define ON HIGH

unsigned long DELAY = 0;

void setup() {
  //unsigned long tmp=5UL*1000*60+30*1000;
  //EEPROM.put(ADDRESS,tmp);
  Serial.begin(115200);
  Serial.println("To set the delay, type 'SET_TIME x', where x is the number of milliseconds to wait");
  EEPROM.get(ADDRESS, DELAY);
  Serial.print("DELAY: ");
  Serial.print(DELAY);
  Serial.print(" (");
  printTime(DELAY);
  Serial.println(" )");
  pinMode(RELAIS, OUTPUT);
  digitalWrite(RELAIS, OFF);
}

void printTime(unsigned long in) {
  int mins = (int)(in / (1000UL * 60));
  Serial.print(mins);
  Serial.print(" Minutes, ");
  double secs = (in - (mins * 1000UL * 60)) / 1000.0;
  Serial.print(secs);
  Serial.print(" Seconds");
}

bool RELAIS_STATE=OFF;
void SET_RELAIS(bool state){
  digitalWrite(RELAIS,state);
  RELAIS_STATE=state;
}

void loop() {
  if (Serial.available()) {
    String cmd = Serial.readString();
    if (cmd.startsWith("SET_TIME")) {
      cmd.replace("SET_TIME ", "");
      cmd.replace("\r", "");
      cmd.replace("\n", "");
      /*Serial.print("using '");
      Serial.print(cmd);
      Serial.println("' as new timestamp");*/
      unsigned long temp = cmd.toInt();
      /*Serial.print("integer rep: ");
      Serial.println(temp);*/
      if (temp < 1000) {
        Serial.println("ERROR - sub-second time is NOT supported");
      }
      else if (temp < 5 * 1000 * 60UL) {
        Serial.println("WARNING - delay is under 5 minutes. Do you actually want that? (YES/NO)");
        while(!Serial.available()){
          delay(100);
        }
        String resp = Serial.readString();
        resp.replace("\r","");
        resp.replace("\n","");
        if (resp.equals("YES")) {
          DELAY = temp;
          EEPROM.put(ADDRESS,DELAY);
          Serial.println("SET INITIAL DELAY AS: ");
          printTime(DELAY);
          Serial.println();
          SET_RELAIS(OFF);
          Serial.println("SET RELAIS TO OFF");
          Serial.println("DONE");
        }
        else {
          //Serial.println("discarded input");
          Serial.println("CANCELLED");
        }
      }
      else{
        DELAY=temp;
          EEPROM.put(ADDRESS,DELAY);
          Serial.println("SET INITIAL DELAY AS: ");
          printTime(DELAY);
          Serial.println();
          SET_RELAIS(OFF);
          Serial.println("SET RELAIS TO OFF");
          Serial.println("DONE");
      }
    }
    else if(cmd.startsWith("VERSION")){
      Serial.println("1.0.0.0");
    }
    else{
      Serial.print("INVALID INPUT: ");
      Serial.println(cmd);
    }
  }
  if ((RELAIS_STATE == OFF) && (millis() > DELAY)) {
    SET_RELAIS(ON);
          Serial.println("SET RELAIS TO ON");
  }
  delay(100);
}
