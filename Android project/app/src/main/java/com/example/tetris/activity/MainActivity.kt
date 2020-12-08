package com.example.tetris.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.tetris.GameViewModel
import com.example.tetris.R
import com.example.tetris.ViewModelFactory
import com.example.tetris.activity.GameActivity.Companion.IP
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*


//Menu activity
class MainActivity : AppCompatActivity() {
    private lateinit var model: GameViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    var TAG = MainActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide()
        viewModelFactory = ViewModelFactory()
        model = ViewModelProvider(this,viewModelFactory).get(GameViewModel::class.java)
        importSettings()

        settings.setOnClickListener {
            Intent(this, SettingsActivity::class.java).apply { startActivity(this) }
        }

        play.setOnClickListener {
            var containsLetters = false
            for(char in ip_edittext.text.toString().toCharArray()){
                if(!char.isDigit() && char != '.'){
                    containsLetters = true
                    break
                }
            }
            if(containsLetters){
                Toast.makeText(applicationContext,"IP cannot contain letters!", Toast.LENGTH_SHORT).show()
            }
            else if(ip_edittext.toString() == ""){
                Toast.makeText(applicationContext,"IP cannot be empty!", Toast.LENGTH_SHORT).show()
            }
            else{
                Intent(
                    this,
                    GameActivity::class.java
                ).apply {
                    this.putExtra(IP,ip_edittext.text.toString())
                    startActivity(this)


                    var player = model.getSongPlayer(applicationContext)
                    player?.let {
                        if(model.getPlaySong()){
                            it.start()
                        }}}
            }
        }
        highscore.setOnClickListener {
            Intent(
                this,
                ScoreboardActivity::class.java
            ).apply { startActivity(this) }
        }
    }

    private fun importSettings() {
        val preferences = getDefaultSharedPreferences(applicationContext)
        model.setPlaySong(preferences.getBoolean("music",true))
    }
}