package com.example.tetris.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.tetris.GameViewModel
import com.example.tetris.R
import com.example.tetris.ViewModelFactory
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var model: GameViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    var TAG = SettingsActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        this.supportActionBar?.hide()
        viewModelFactory = ViewModelFactory()
        model = ViewModelProvider(this,viewModelFactory).get(GameViewModel::class.java)
        music_switch.isChecked = model.getPlaySong()

        music_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            model.setPlaySong(isChecked)
            val editor = getDefaultSharedPreferences(applicationContext).edit()
            editor.putBoolean("music",isChecked)
            editor.commit()
            Log.i(TAG,"Music setting switched and commited")
        }
    }
}