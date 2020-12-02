package com.example.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SimpleAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var model:GameViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        this.supportActionBar?.hide()
        viewModelFactory = ViewModelFactory()
        model = ViewModelProvider(this,viewModelFactory).get(GameViewModel::class.java)

        music_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            model.setPlaySong(isChecked)
            var player = model.getSongPlayer(applicationContext)
            player?.let {
                if(player.isPlaying && !isChecked){
                    player.stop()
                    model.nullPlayer()
                }
            }
        }
    }
}