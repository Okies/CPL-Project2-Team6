#include <SoftwareSerial.h>
#include <Servo.h>

// 서보모터 변수
Servo myservo;  
int servoPin = 8;
bool isOpen = false;

// 블루투스 변수
int blueTx = 2;
int blueRx = 3;
SoftwareSerial mySerial(blueTx, blueRx);
String myString = "";
  
void setup() {  
  Serial.begin(9600);  
  mySerial.begin(9600);
  myservo.attach(servoPin);
  
}  

void loop() {  
    myservo.write(88);
    delay(20);
  while(mySerial.available())
  {
    char blueChar = (char)mySerial.read();
    myString += blueChar;
    delay(5);
  }
  if(myString == "open" && !isOpen)
  {
    Serial.println("Door unlock");
    myservo.write(180);
    delay(180);
    myservo.write(88);
    isOpen = true;
  }
  else if(myString == "close" && isOpen)
  {
    Serial.println("Door lock");
    myservo.write(0);
    delay(180);
    myservo.write(88);
    isOpen = false;
  }
    myString = "";
} 
