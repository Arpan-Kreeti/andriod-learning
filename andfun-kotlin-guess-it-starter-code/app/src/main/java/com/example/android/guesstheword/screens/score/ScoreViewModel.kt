package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ScoreViewModel(finalScore: Int) : ViewModel() {

    private val _score = MutableLiveData<Int>()

    private val _eventPlayAgain = MutableLiveData<Boolean>()

    init {
        _score.value = finalScore
        Log.i("ScoreViewModel", "Final score is $finalScore")
    }



    val score: LiveData<Int>
        get() = _score

    val eventPlayAgain: LiveData<Boolean>
        get() = _eventPlayAgain

    fun onPlayAgain() {
        Log.i("ScoreViewModel","Playing again")
        _eventPlayAgain.value = true
    }

    fun resetEventPlayAgain() {
        _eventPlayAgain.value = false
    }
}