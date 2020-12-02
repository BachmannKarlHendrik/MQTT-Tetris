avas#include <WiFi.h>
#include <PubSubClient.h>
#define A 34
#define B 35
const char* ssid     = "MegaAeglane";
const char* password = "KarlSööbJäätist";
const char* mqtt_server = "192.168.1.214";


WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);


void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  pinMode(A, INPUT);
  pinMode(B, INPUT);
  delay(10);

    // We start by connecting to a WiFi network
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);

    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());

    // Set up MQTT
    mqttClient.setServer(mqtt_server, 1883);

}

void reconnect() {
  // Loop until we're reconnected
  while (!mqttClient.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP32Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (mqttClient.connect(clientId.c_str())) {
      Serial.println("Connected");
      // Connected - do something useful - subscribe to topics, publish messages, etc.
    } else {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}


String command = "";
String lastCommand = "";
int counter = 0;
void loop() {
  // put your main code here, to run repeatedly:

  if (!mqttClient.connected()) {
    reconnect();
  }

  mqttClient.loop();
  
  int x = analogRead(A);
  int y = analogRead(B);
  if(y == 0){
    command = "Left";
  }
  else if(x < 3000 && y > 3000){
    command = "Right";
  }
  else if(x == 0){
    command = "Up";
  }
  else if(x > 3000 && y < 3000){
    command = "Down";
  }
  else{
    command = "";
    lastCommand = "";
  }
  counter ++;
  if((!command.equals(lastCommand) && !command.equals("")) || counter >= 500){
    Serial.println((char*) command.c_str());
    lastCommand = command;
    counter = 0;
    mqttClient.publish("Tetris",  (char*) command.c_str());
  }
  else{
    Serial.println("");
  }
  Serial.println(x);
  Serial.println(y);
  Serial.println("**************");
  delay(50);
}
