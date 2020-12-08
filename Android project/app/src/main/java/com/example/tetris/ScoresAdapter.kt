package com.example.tetris

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tetris.entities.ScoreEntity
import kotlinx.android.synthetic.main.single_score.view.*

class ScoresAdapter : RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>() {
    var data =  ArrayList<ScoreEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_score,parent,false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = data[position]

        holder.itemView.apply {
            place_txt.text = (data.indexOf(score) + 1).toString()
            name_txt.text = score.player
            score_txt.text = score.score.toString()
        }
    }

    override fun getItemCount(): Int = data.size
}