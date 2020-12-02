package com.example.tetris

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


//Menu activity
class MainActivity : AppCompatActivity() {
    private lateinit var model:GameViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide()
        viewModelFactory = ViewModelFactory()
        model = ViewModelProvider(this,viewModelFactory).get(GameViewModel::class.java)

        settings.setOnClickListener {
            Intent(this, SettingsActivity::class.java).apply { startActivity(this) }
        }

        play.setOnClickListener {
            Intent(
                this,
                GameActivity::class.java
            ).apply { startActivity(this)

                var player = model.getSongPlayer(applicationContext)
            player?.let {
                if(model.getPlaySong()){
                    it.start()
                }}}
        }
        highscore.setOnClickListener {
            Intent(
                this,
                ScoreboardActivity::class.java
            ).apply { startActivity(this) }
        }
    }
}