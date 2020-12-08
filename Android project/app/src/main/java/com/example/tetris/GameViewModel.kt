package com.example.tetris

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tetris.R
import com.example.tetris.entities.ScoreEntity
import com.example.tetris.firestore.FirestoreDataRepository
import com.google.firebase.firestore.Query
import kotlin.random.Random

open class GameViewModel : ViewModel() {
    var TAG = GameViewModel::class.qualifiedName
    private val repository = FirestoreDataRepository()
    private var scoreLiveDataList: MutableLiveData<ArrayList<ScoreEntity>> = MutableLiveData()
    private var mediaPlayer: MediaPlayer? = null
    private var playSong = true
    var scoreArray = ArrayList<ScoreEntity>()

    fun getSongPlayer(context: Context): MediaPlayer? {
        if (mediaPlayer == null) {
            if (Random.nextDouble() < 0.98) mediaPlayer = MediaPlayer.create(
                context,
                R.raw.themesongfix
            )
            else mediaPlayer = MediaPlayer.create(context, R.raw.beatboxfix) // easter egg
            mediaPlayer?.isLooping = true
            return mediaPlayer
        } else {
            return mediaPlayer
        }
    }

    fun setPlaySong(boolean: Boolean) {
        playSong = boolean
    }

    fun getPlaySong(): Boolean {
        return playSong
    }

    fun nullPlayer() {
        mediaPlayer = null
    }

    fun getScores(): LiveData<ArrayList<ScoreEntity>> {
        val scoresArrayTemp = ArrayList<ScoreEntity>()
        repository.getAllScores().orderBy("score", Query.Direction.DESCENDING).limitToLast(100)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.wtf(TAG, "getScores error")
                    return@addSnapshotListener
                } else if (value != null) {
                    for (doc in value) {
                        scoresArrayTemp.add(
                            ScoreEntity(
                                doc.id,
                                doc.getString("player"),
                                Integer.parseInt(doc.get("score").toString())
                            )
                        )
                    }
                    scoreLiveDataList.value = scoresArrayTemp
                }
            }
        return scoreLiveDataList
    }

    fun saveScore(score: ScoreEntity, context: Context) {
        repository.saveScore(score).addOnCompleteListener { return@addOnCompleteListener }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Error: Saving failed!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}