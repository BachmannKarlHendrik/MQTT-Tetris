package com.example.tetris

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import kotlin.random.Random

open class GameViewModel: ViewModel(){
    private var runningScore: Int = 0
    private var mediaPlayer:MediaPlayer? = null
    private var playSong = true

    fun saveRunningScore(score: Int){
        runningScore = score
    }

    fun getRunningScore():Int{
        return runningScore
    }

    fun getSongPlayer(context: Context): MediaPlayer?{
        if(mediaPlayer == null){
            if (Random.nextDouble() < 0.98) mediaPlayer = MediaPlayer.create(context,R.raw.themesongfix)
            else mediaPlayer = MediaPlayer.create(context,R.raw.beatboxfix) // easter egg
            mediaPlayer?.isLooping = true
            return mediaPlayer
        }
        else{
            return mediaPlayer
        }
    }

    fun setPlaySong(boolean: Boolean){
        playSong = boolean
    }

    fun getPlaySong():Boolean{
        return playSong
    }

    fun nullPlayer(){
        mediaPlayer = null
    }
}