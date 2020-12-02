package com.example.tetris

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import kotlinx.android.synthetic.main.activity_main.*


//Menu activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide()

        settings.setOnClickListener {
            Intent(this, SettingsActivity::class.java).apply { startActivity(this) }
        }

        play.setOnClickListener {
            Intent(
                this,
                GameActivity::class.java
            ).apply { startActivity(this) }
        }
        highscore.setOnClickListener { Intent(this, highscore::class.java) }
    }
}