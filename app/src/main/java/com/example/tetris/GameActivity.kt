package com.example.tetris

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.Exception

class GameActivity : AppCompatActivity() {
    private val MQTT_BROKER_IP = "tcp://192.168.1.214:1883"
    val TAG = "MqttActivity"
    lateinit var mqttClient: MqttAndroidClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)// set up client
        this.supportActionBar?.hide()
        setupMqtt()
    }

    override fun onPause() {
        mqttClient.disconnect()
        super.onPause()
    }

    override fun onDestroy() {
        mqttClient.disconnect()
        super.onDestroy()

    }

    private fun setupMqtt(){
        mqttClient = MqttAndroidClient(this, MQTT_BROKER_IP, "lab11client")
        mqttClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.i(TAG, "MQTT Connected")
                Toast.makeText(getApplicationContext(),"MQTT connected", Toast.LENGTH_SHORT).show()
                mqttClient.subscribe("karl",0) //TODO setup mqtt
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.i(TAG, "MQTT Message: $topic, msg: ${message.toString()}")
                //TODO Message parsing
            }

            override fun connectionLost(cause: Throwable?) {
                Log.i(TAG, "MQTT Connection Lost!")
                Toast.makeText(getApplicationContext(),"MQTT disconnected", Toast.LENGTH_SHORT).show()
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.i(TAG, "MQTT Message Delivered!")
            }
        })

        mqttClient.connect()
    }

    override fun onResume() {
        setupMqtt()
        super.onResume()
    }
}