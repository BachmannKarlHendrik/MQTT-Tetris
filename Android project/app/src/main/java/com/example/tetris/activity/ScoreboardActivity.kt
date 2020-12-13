package com.example.tetris.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tetris.GameViewModel
import com.example.tetris.R
import com.example.tetris.ScoresAdapter
import com.example.tetris.ViewModelFactory
import com.example.tetris.entities.ScoreEntity
import kotlinx.android.synthetic.main.activity_scoreboard.*

class ScoreboardActivity : AppCompatActivity() {
    private lateinit var model: GameViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var observer: Observer<ArrayList<ScoreEntity>>
    private lateinit var scoresAdapter: ScoresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        this.supportActionBar?.hide()
        viewModelFactory = ViewModelFactory()
        model = ViewModelProvider(this,viewModelFactory).get(GameViewModel::class.java)
        observer = createObserver()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        scoresAdapter = ScoresAdapter()
        scores_recyclerView.adapter = scoresAdapter
        scores_recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateScoreList() {
        val liveData = model.getScores()
        liveData.observe(this,observer)
    }

    private fun createObserver(): Observer<ArrayList<ScoreEntity>> {
        return Observer<ArrayList<ScoreEntity>> { scores ->
            if(scores != null){
                var contains = false
                for(score in scores) {
                    for(added in model.scoreArray){
                        if(score.id == added.id){
                            contains = true
                        }
                    }
                    if(!contains) {
                        var added = false
                        val thisScore = score.score
                        for (i in 0 until model.scoreArray.size) {
                            val otherScore = model.scoreArray[i].score
                            if (thisScore != null && otherScore != null) {
                                if (otherScore < thisScore){
                                    model.scoreArray.add(i,score)
                                    added = true
                                    break
                                }
                            }
                        }
                        if(added == false){
                            model.scoreArray.add(score)
                        }
                        Log.i("Observer", "New score added to scoreArray")
                    }
                }
                scoresAdapter.notifyDataSetChanged()
                inTotal_text.text = resources.getQuantityString(R.plurals.score,model.scoreArray.size,model.scoreArray.size)
            }


        }

    }

    override fun onResume() {
        super.onResume()
        model.scoreArray = ArrayList()
        updateScoreList()
        scoresAdapter.data = model.scoreArray
    }


}