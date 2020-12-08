package com.example.tetris.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.tetris.GameViewModel
import com.example.tetris.R
import com.example.tetris.ViewModelFactory
import com.example.tetris.entities.ScoreEntity
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.music_switch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class GameActivity : AppCompatActivity() {

    companion object{
        const val IP = "ip"
    }

    private lateinit var MQTT_BROKER_IP:String // = "tcp://192.168.1.214:1883"
    val TAG = "MqttActivity"
    lateinit var mqttClient: MqttAndroidClient
    private lateinit var model: GameViewModel
    private lateinit var viewModelFactory: ViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        var ip = intent.getStringExtra(IP)
            if (ip != null) {
                MQTT_BROKER_IP = "tcp://$ip:1883"
            }
        supportActionBar?.hide()
        viewModelFactory = ViewModelFactory()
        game_canvas.sendButton(gameover_btn)
        model = ViewModelProvider(this,viewModelFactory).get(GameViewModel::class.java)


        pause_btn.setOnClickListener {
            game_canvas.isPaused = true
            togglePauseScreen()
            Log.i(TAG,MQTT_BROKER_IP.toString())
        }

        resumebtn.setOnClickListener {
            game_canvas.isPaused = false
            togglePauseScreen()
        }


        mainmenubtn.setOnClickListener { finish() }
        cancel_btn.setOnClickListener{ finish() }

        save_btn.setOnClickListener {
            val name = name_edittext.text.toString()
            if(name == ""){
                Toast.makeText(applicationContext,"Name can't be empty!", Toast.LENGTH_SHORT).show()
            }
            else{
                model.saveScore(ScoreEntity("0",name,game_canvas.score),applicationContext)
                finish()
            }

        }

        gameover_btn.setOnClickListener {
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
                val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                editor.putBoolean("music",isChecked)
                editor.commit()
                Log.i(TAG,"Music setting switched and commited")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupMqtt()
    }

    private fun togglePauseScreen() {
        if(pause_screen.visibility == View.VISIBLE){
            pause_screen.visibility = View.GONE
            pause_btn.visibility = View.VISIBLE
            if(model.getPlaySong() && model.getSongPlayer(applicationContext)?.isPlaying == false) {
                model.getSongPlayer(applicationContext)?.start()
            }
        }
        else{
            pause_screen.visibility = View.VISIBLE
            pause_btn.visibility = View.GONE
            if(model.getPlaySong()) {
                model.getSongPlayer(applicationContext)?.pause()
            }
        }
    }

    private fun gameOverSequence() {
        gameOver_screen.visibility = View.VISIBLE
        pause_btn.visibility = View.GONE
        gameOverText.text = "Game over!\nScore: " + game_canvas.score.toString()
        if(model.getPlaySong()) {
            model.getSongPlayer(applicationContext)?.pause()
        }
    }

    override fun onPause() {
        if(mqttClient.isConnected) {
            mqttClient.disconnect()
        }
        if(!game_canvas.gameOver) {
            game_canvas.isPaused = true //Make sure that the game is on pause
            pause_screen.visibility = View.VISIBLE
        }
        if(model.getPlaySong() && model.getSongPlayer(applicationContext)?.isPlaying == true) {
            model.getSongPlayer(applicationContext)?.pause()
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
        if(pause_screen.visibility == View.VISIBLE || game_canvas.gameOver){
            super.onBackPressed()
        }
        else{
            game_canvas.isPaused = true
            togglePauseScreen()
        }

    }
}
