package com.example.tetris

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
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
        setContentView(R.layout.activity_game)// set up client
        this.supportActionBar?.hide()
        setupMqtt()

        pauseBtn.setOnClickListener {
            gamecanvas.isPaused = true
            togglePauseScreen() }

        resumebtn.setOnClickListener {
            gamecanvas.isPaused = false
            togglePauseScreen() }
        mainmenubtn.setOnClickListener { finish() }
    }

    private fun togglePauseScreen() {
        if(resumebtn.visibility == View.VISIBLE){
            resumebtn.visibility = View.GONE
            mainmenubtn.visibility = View.GONE
            pauseText.visibility = View.GONE
            pauseBtn.visibility = View.VISIBLE
        }
        else{
            resumebtn.visibility = View.VISIBLE
            mainmenubtn.visibility = View.VISIBLE
            pauseText.visibility = View.VISIBLE
            pauseBtn.visibility = View.GONE
        }
    }

    override fun onPause() {
        if(mqttClient.isConnected) {
            mqttClient.disconnect()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if(mqttClient.isConnected) {
            mqttClient.disconnect()
        }
        super.onDestroy()

    }

    private fun setupMqtt(){
        mqttClient = MqttAndroidClient(this, MQTT_BROKER_IP, "lab11client")
        mqttClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.i(TAG, "MQTT Connected")
                Toast.makeText(applicationContext,"MQTT connected", Toast.LENGTH_SHORT).show()
                mqttClient.subscribe("Tetris",0)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.i(TAG, "MQTT Message: $topic, msg: ${message.toString()}")
                gamecanvas.remote(message.toString())
            }

            override fun connectionLost(cause: Throwable?) {
                Log.i(TAG, "MQTT Connection Lost!")
                Toast.makeText(applicationContext,"MQTT disconnected", Toast.LENGTH_SHORT).show()
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