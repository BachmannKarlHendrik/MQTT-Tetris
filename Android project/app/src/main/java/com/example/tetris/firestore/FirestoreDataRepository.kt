package com.example.tetris.firestore

import com.example.tetris.entities.ScoreEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreDataRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllScores(): CollectionReference {
        val ref = firestore.collection("scores")
        return ref
    }

    fun saveScore(newScore: ScoreEntity): Task<DocumentReference> {
        val ref = firestore.collection("scores")
        return ref.add(hashMapOf(
            "player" to newScore.player,
            "score" to newScore.score))
    }

}