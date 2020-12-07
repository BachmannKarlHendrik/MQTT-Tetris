package com.example.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.music_switch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class GameActivity : AppCompatActivity() {
    //private val MQTT_BROKER_IP = "tcp://192.168.1.214:1883" //phoneK
    //private val MQTT_BROKER_IP = "tcp://192.168.1.196:1883" //phoneS
    private val MQTT_BROKER_IP = "tcp://10.0.2.2:1883" //Emulaator
    val TAG = "MqttActivity"
    lateinit var mqttClient: MqttAndroidClient
    private lateinit var model: GameViewModel
    private lateinit var viewModelFactory: ViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)// set up client
        supportActionBar?.hide()
        viewModelFactory = ViewModelFactory()
        model = ViewModelProvider(this,viewModelFactory).get(GameViewModel::class.java)


        pause_btn.setOnClickListener {
            game_canvas.isPaused = true
            togglePauseScreen()
        }

        resume_btn.setOnClickListener {
            game_canvas.isPaused = false
            togglePauseScreen()
        }

        mainmenu_btn.setOnClickListener { finish() }

        save_btn.setOnClickListener {
            val name = name_edittext.text
            //TODO: SaveToFirebase
            finish()
        }

        cancel_btn.setOnClickListener{ finish() }


        val gameoverBtn = findViewById<Button>(R.id.gameover_btn)
        game_canvas.sendButton(gameoverBtn)
        gameoverBtn.setOnClickListener {
            gameOverSequence()
        }

        music_switch.isChecked = model.getPlaySong()
        music_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            model.setPlaySong(isChecked)
            var player = model.getSongPlayer(applicationContext)
            player?.let {
                if(player.isPlaying && !isChecked){
                    player.stop()
                    model.nullPlayer()
                }
                else if(!player.isPlaying && isChecked){
                    player.start()
                }
            }
        }
    }

    override fun onResume() {
        setupMqtt()
        super.onResume()
    }

    private fun togglePauseScreen() {
        if(resume_btn.visibility == View.VISIBLE){
            resume_btn.visibility = View.GONE
            mainmenu_btn.visibility = View.GONE
            music_switch.visibility = View.GONE
            music_text.visibility = View.GONE
            pause_btn.visibility = View.VISIBLE
        }
        else{
            resume_btn.visibility = View.VISIBLE
            mainmenu_btn.visibility = View.VISIBLE
            music_switch.visibility = View.VISIBLE
            music_text.visibility = View.VISIBLE
            pause_btn.visibility = View.GONE
        }
    }

    private fun gameOverSequence() {
        pause_btn.visibility = View.GONE
        name_edittext.visibility = View.VISIBLE
        save_btn.visibility = View.VISIBLE
        cancel_btn.visibility = View.VISIBLE
    }

    override fun onPause() {
        if(mqttClient.isConnected) {
            mqttClient.disconnect()
        }
        game_canvas.isPaused = true
        resume_btn.visibility = View.VISIBLE
        mainmenu_btn.visibility = View.VISIBLE
        music_switch.visibility = View.VISIBLE
        music_text.visibility = View.VISIBLE
        pause_btn.visibility = View.GONE
        super.onPause()
    }

    override fun onDestroy() {
        if(mqttClient.isConnected) {
            mqttClient.disconnect()
        }
        super.onDestroy()

    }

    private fun setupMqtt(){
        mqttClient = MqttAndroidClient(this, MQTT_BROKER_IP, "TetrisProjectClient")
        mqttClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.i(TAG, "MQTT Connected")
                Toast.makeText(applicationContext,"MQTT connected", Toast.LENGTH_SHORT).show()
                mqttClient.subscribe("Tetris",0)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (topic == "Tetris"){
                    Log.i(TAG, "MQTT Message: $topic, msg: ${message.toString()}")
                    game_canvas.remote(message.toString())

                }
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


    override fun onBackPressed() {
        if(resume_btn.visibility == View.VISIBLE){
            super.onBackPressed()
        }
        else{
            game_canvas.isPaused = true
            resume_btn.visibility = View.VISIBLE
            mainmenu_btn.visibility = View.VISIBLE
            pause_btn.visibility = View.GONE
        }

    }
}
